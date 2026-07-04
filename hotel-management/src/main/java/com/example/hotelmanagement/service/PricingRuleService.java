package com.example.hotelmanagement.service;

import com.example.hotelmanagement.dto.request.PricingRuleRequest;
import com.example.hotelmanagement.dto.response.PricingRuleResponse;

import java.util.List;

public interface PricingRuleService {
    List<PricingRuleResponse> getAllPricingRules();
    List<PricingRuleResponse> getActiveRulesByRoomType(Integer roomTypeId);
    PricingRuleResponse getPricingRuleById(Integer id);
    PricingRuleResponse createPricingRule(PricingRuleRequest request);
    PricingRuleResponse updatePricingRule(Integer id, PricingRuleRequest request);
    void deletePricingRule(Integer id);
}
