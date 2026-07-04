package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private Integer id;
    private Integer roomTypeId;
    private String roomTypeName;
    private String roomNumber;
    private Integer floorNumber;
    private String status;
}
