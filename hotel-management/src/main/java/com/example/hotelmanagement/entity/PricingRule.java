package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false, length = 20)
    private String rentalType; // HOURLY_FIRST, HOURLY_NEXT, OVERNIGHT, DAILY

    @Column(nullable = false, precision = 18, scale = 0)
    private BigDecimal price;

    @Column(name = "is_seasonal")
    private Boolean isSeasonal;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "is_active")
    private Boolean isActive;
    
    @PrePersist
    public void prePersist() {
        if (isSeasonal == null) isSeasonal = false;
        if (effectiveDate == null) effectiveDate = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
}
