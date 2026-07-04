package com.example.hotelmanagement.service.impl;

import com.example.hotelmanagement.dto.request.PricingRuleRequest;
import com.example.hotelmanagement.dto.response.PricingRuleResponse;
import com.example.hotelmanagement.entity.PricingRule;
import com.example.hotelmanagement.entity.RoomType;
import com.example.hotelmanagement.repository.PricingRuleRepository;
import com.example.hotelmanagement.repository.RoomTypeRepository;
import com.example.hotelmanagement.service.PricingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingRuleServiceImpl implements PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<PricingRuleResponse> getAllPricingRules() {
        return pricingRuleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PricingRuleResponse> getActiveRulesByRoomType(Integer roomTypeId) {
        return pricingRuleRepository.findByRoomTypeIdAndIsActiveTrue(roomTypeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PricingRuleResponse getPricingRuleById(Integer id) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PricingRule not found"));
        return mapToResponse(rule);
    }

    @Override
    public PricingRuleResponse createPricingRule(PricingRuleRequest request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        PricingRule rule = PricingRule.builder()
                .roomType(roomType)
                .rentalType(request.getRentalType())
                .price(request.getPrice())
                .isSeasonal(request.getIsSeasonal() != null ? request.getIsSeasonal() : false)
                .effectiveDate(request.getEffectiveDate() != null ? request.getEffectiveDate() : LocalDateTime.now())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        rule = pricingRuleRepository.save(rule);
        return mapToResponse(rule);
    }

    @Override
    public PricingRuleResponse updatePricingRule(Integer id, PricingRuleRequest request) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PricingRule not found"));

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("RoomType not found"));

        rule.setRoomType(roomType);
        rule.setRentalType(request.getRentalType());
        rule.setPrice(request.getPrice());
        
        if (request.getIsSeasonal() != null) rule.setIsSeasonal(request.getIsSeasonal());
        if (request.getEffectiveDate() != null) rule.setEffectiveDate(request.getEffectiveDate());
        if (request.getIsActive() != null) rule.setIsActive(request.getIsActive());

        rule = pricingRuleRepository.save(rule);
        return mapToResponse(rule);
    }

    @Override
    public void deletePricingRule(Integer id) {
        if (!pricingRuleRepository.existsById(id)) {
            throw new RuntimeException("PricingRule not found");
        }
        pricingRuleRepository.deleteById(id);
    }

    private PricingRuleResponse mapToResponse(PricingRule rule) {
        return PricingRuleResponse.builder()
                .id(rule.getId())
                .roomTypeId(rule.getRoomType().getId())
                .roomTypeName(rule.getRoomType().getTypeName())
                .rentalType(rule.getRentalType())
                .price(rule.getPrice())
                .isSeasonal(rule.getIsSeasonal())
                .effectiveDate(rule.getEffectiveDate())
                .isActive(rule.getIsActive())
                .build();
    }
}
