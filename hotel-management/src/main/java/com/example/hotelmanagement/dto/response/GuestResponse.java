package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestResponse {
    private Integer id;
    private String fullName;
    private String identityNumber;
    private String phone;
    private Integer userId;
    private String username;
}
