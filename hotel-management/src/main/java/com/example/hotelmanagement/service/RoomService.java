package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.RoomRequest;
import com.example.hotelmanagement.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomById(Integer id);
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Integer id, RoomRequest request);
    void deleteRoom(Integer id);
    RoomResponse updateRoomStatus(Integer id, String status);
    RoomResponse completeCleaning(Integer id);
}
