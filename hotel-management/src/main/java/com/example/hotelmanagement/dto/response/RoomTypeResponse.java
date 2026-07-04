package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomTypeResponse {
    private Integer id;
    private String typeName;
}
