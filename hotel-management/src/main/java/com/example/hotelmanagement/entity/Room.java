package com.example.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floorNumber;

    @Column(nullable = false, length = 20)
    private String status; // AVAILABLE, OCCUPIED, DIRTY, MAINTENANCE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_cleaned_by")
    private User lastCleanedBy;

    @Column(name = "last_cleaned_time")
    private java.time.LocalDateTime lastCleanedTime;
}
