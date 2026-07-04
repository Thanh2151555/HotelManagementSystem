package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.RoomRequest;
import com.example.hotelmanagement.dto.response.RoomResponse;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.RoomRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import com.example.hotelmanagement.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return mapToResponse(room);
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found"));
                
        Room room = Room.builder()
                .roomType(roomType)
                .roomNumber(request.getRoomNumber())
                .floorNumber(request.getFloorNumber())
                .status(request.getStatus() != null ? request.getStatus() : "AVAILABLE")
                .build();
                
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Override
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
                
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        room.setRoomType(roomType);
        room.setRoomNumber(request.getRoomNumber());
        room.setFloorNumber(request.getFloorNumber());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Override
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }
        roomRepository.deleteById(id);
    }

    @Override
    public RoomResponse updateRoomStatus(Integer id, String status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setStatus(status);
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    @Override
    public RoomResponse completeCleaning(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        if (!"DIRTY".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Only DIRTY rooms can be cleaned");
        }
        
        room.setStatus("AVAILABLE");
        room = roomRepository.save(room);
        return mapToResponse(room);
    }

    private RoomResponse mapToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomTypeId(room.getRoomType().getId())
                .roomTypeName(room.getRoomType().getTypeName())
                .roomNumber(room.getRoomNumber())
                .floorNumber(room.getFloorNumber())
                .status(room.getStatus())
                .build();
    }
}
