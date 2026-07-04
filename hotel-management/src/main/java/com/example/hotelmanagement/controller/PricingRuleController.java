package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.PricingRuleRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.PricingRuleResponse;
import com.example.hotelmanagement.service.PricingRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
@RequiredArgsConstructor
public class PricingRuleController {

    private final PricingRuleService pricingRuleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PricingRuleResponse>>> getAllPricingRules() {
        return ResponseEntity.ok(ApiResponse.success(pricingRuleService.getAllPricingRules()));
    }

    // Lấy danh sách bảng giá đang kích hoạt của một loại phòng cụ thể (dành cho màn hình Đặt phòng)
    @GetMapping("/room-types/{roomTypeId}/active")
    public ResponseEntity<ApiResponse<List<PricingRuleResponse>>> getActiveRulesByRoomType(@PathVariable Integer roomTypeId) {
        return ResponseEntity.ok(ApiResponse.success(pricingRuleService.getActiveRulesByRoomType(roomTypeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> getPricingRuleById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(pricingRuleService.getPricingRuleById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PricingRuleResponse>> createPricingRule(@Valid @RequestBody PricingRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created successfully", pricingRuleService.createPricingRule(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PricingRuleResponse>> updatePricingRule(@PathVariable Integer id, @Valid @RequestBody PricingRuleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", pricingRuleService.updatePricingRule(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePricingRule(@PathVariable Integer id) {
        pricingRuleService.deletePricingRule(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}
