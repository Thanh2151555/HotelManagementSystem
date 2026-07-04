package com.example.hotelmanagement.repository;

import com.example.hotelmanagement.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Integer> {
    List<PricingRule> findByRoomTypeId(Integer roomTypeId);
    List<PricingRule> findByRoomTypeIdAndIsActiveTrue(Integer roomTypeId);
    java.util.Optional<PricingRule> findByRoomTypeIdAndRentalTypeAndIsActiveTrue(Integer roomTypeId, String rentalType);
}
