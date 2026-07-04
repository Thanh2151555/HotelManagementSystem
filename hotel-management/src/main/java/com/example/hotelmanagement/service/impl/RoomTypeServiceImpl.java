package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.RoomTypeRequest;
import com.example.hotelmanagement.dto.response.RoomTypeResponse;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import com.example.hotelmanagement.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found"));
        return mapToResponse(roomType);
    }

    @Override
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {
        RoomType roomType = RoomType.builder()
                .typeName(request.getTypeName())
                .build();
        roomType = roomTypeRepository.save(roomType);
        return mapToResponse(roomType);
    }

    @Override
    public RoomTypeResponse updateRoomType(Integer id, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RoomType not found"));
        roomType.setTypeName(request.getTypeName());
        roomType = roomTypeRepository.save(roomType);
        return mapToResponse(roomType);
    }

    @Override
    public void deleteRoomType(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RuntimeException("RoomType not found");
        }
        roomTypeRepository.deleteById(id);
    }

    private RoomTypeResponse mapToResponse(RoomType roomType) {
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .typeName(roomType.getTypeName())
                .build();
    }
}
