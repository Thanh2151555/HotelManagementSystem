package com.example.hotelmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String roleName;
}
