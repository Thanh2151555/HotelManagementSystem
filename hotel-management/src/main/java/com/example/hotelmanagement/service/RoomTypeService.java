package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.RoomTypeRequest;
import com.example.hotelmanagement.dto.response.RoomTypeResponse;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeResponse> getAllRoomTypes();
    RoomTypeResponse getRoomTypeById(Integer id);
    RoomTypeResponse createRoomType(RoomTypeRequest request);
    RoomTypeResponse updateRoomType(Integer id, RoomTypeRequest request);
    void deleteRoomType(Integer id);
}
