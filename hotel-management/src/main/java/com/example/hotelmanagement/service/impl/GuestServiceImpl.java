package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.GuestRequest;
import com.example.hotelmanagement.dto.response.GuestResponse;
import com.example.hotelmanagement.dto.response.PageResponse;
import com.example.hotelmanagement.entity.Guest;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.repository.GuestRepository;
import com.example.hotelmanagement.repository.UserRepository;
import com.example.hotelmanagement.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<GuestResponse> searchGuests(String keyword, int page, int size) {
        // Ràng buộc nghiệp vụ: Một trang không hiển thị quá 20 bản ghi
        if (size > 20) {
            size = 20; 
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Guest> guestPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            guestPage = guestRepository.searchGuests(keyword, pageable);
        } else {
            guestPage = guestRepository.findAll(pageable);
        }

        List<GuestResponse> content = guestPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<GuestResponse>builder()
                .content(content)
                .pageNumber(guestPage.getNumber())
                .pageSize(guestPage.getSize())
                .totalElements(guestPage.getTotalElements())
                .totalPages(guestPage.getTotalPages())
                .isLast(guestPage.isLast())
                .build();
    }

    @Override
    public GuestResponse getGuestById(Integer id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        return mapToResponse(guest);
    }

    @Override
    public GuestResponse createGuest(GuestRequest request) {
        Guest guest = Guest.builder()
                .fullName(request.getFullName())
                .identityNumber(request.getIdentityNumber())
                .phone(request.getPhone())
                .build();
                
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            guest.setUser(user);
        }

        guest = guestRepository.save(guest);
        return mapToResponse(guest);
    }

    @Override
    public GuestResponse updateGuest(Integer id, GuestRequest request) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        guest.setFullName(request.getFullName());
        guest.setIdentityNumber(request.getIdentityNumber());
        guest.setPhone(request.getPhone());
        
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            guest.setUser(user);
        } else {
            guest.setUser(null);
        }

        guest = guestRepository.save(guest);
        return mapToResponse(guest);
    }

    @Override
    public void deleteGuest(Integer id) {
        if (!guestRepository.existsById(id)) {
            throw new RuntimeException("Guest not found");
        }
        guestRepository.deleteById(id);
    }

    private GuestResponse mapToResponse(Guest guest) {
        return GuestResponse.builder()
                .id(guest.getId())
                .fullName(guest.getFullName())
                .identityNumber(guest.getIdentityNumber())
                .phone(guest.getPhone())
                .userId(guest.getUser() != null ? guest.getUser().getId() : null)
                .username(guest.getUser() != null ? guest.getUser().getUsername() : null)
                .build();
    }
}
