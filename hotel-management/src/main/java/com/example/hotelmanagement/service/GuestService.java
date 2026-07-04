package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.GuestRequest;
import com.example.hotelmanagement.dto.response.GuestResponse;
import com.example.hotelmanagement.dto.response.PageResponse;

public interface GuestService {
    PageResponse<GuestResponse> searchGuests(String keyword, int page, int size);
    GuestResponse getGuestById(Integer id);
    GuestResponse createGuest(GuestRequest request);
    GuestResponse updateGuest(Integer id, GuestRequest request);
    void deleteGuest(Integer id);
}
