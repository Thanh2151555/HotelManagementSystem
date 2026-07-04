package com.example.hotelmanagement.config;

import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.hotelmanagement.enums.InvoiceStatus;
import com.example.hotelmanagement.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ServiceRepository serviceRepository;
    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            log.info("Database is empty. Seeding mock data...");
            seedData();
            log.info("Database seeding completed!");
        } else {
            log.info("Database already contains data. Skipping seeding.");
        }
    }

    private void seedData() {
        // 0. Seed Roles & Users
        Role adminRole = Role.builder().name("Admin").build();
        Role ownerRole = Role.builder().name("Owner").build();
        Role receptionRole = Role.builder().name("Receptionist").build();
        Role hkRole = Role.builder().name("Housekeeping").build();
        roleRepository.saveAll(Arrays.asList(adminRole, ownerRole, receptionRole, hkRole));

        String encodedPassword = "$2a$10$PMfT7tiROw58mdLtriwCdeYs0RSRltc9qpHUE7lW9PydIYu0RZmN."; // 123456
        
        User admin = User.builder().role(adminRole).username("admin").password(encodedPassword).fullName("Administrator").isActive(true).build();
        User owner = User.builder().role(ownerRole).username("owner").password(encodedPassword).fullName("Hotel Owner").isActive(true).build();
        User receptionist = User.builder().role(receptionRole).username("reception").password(encodedPassword).fullName("Receptionist").isActive(true).build();
        User housekeeping = User.builder().role(hkRole).username("housekeeping").password(encodedPassword).fullName("Housekeeping").isActive(true).build();
        userRepository.saveAll(Arrays.asList(admin, owner, receptionist, housekeeping));

        // 1. Seed Room Types (Vietnamese typical)
        RoomType singleRoom = RoomType.builder().typeName("Phòng Đơn").build();
        RoomType doubleRoom = RoomType.builder().typeName("Phòng Đôi").build();
        RoomType balconyRoom = RoomType.builder().typeName("Phòng Ban Công").build();
        
        roomTypeRepository.saveAll(Arrays.asList(singleRoom, doubleRoom, balconyRoom));

        // 2. Seed Rooms (25 rooms, Floor 1 to 5, 5 rooms per floor)
        String[] statuses = {"AVAILABLE", "AVAILABLE", "AVAILABLE", "OCCUPIED", "DIRTY"};
        Random random = new Random();
        List<RoomType> types = Arrays.asList(singleRoom, doubleRoom, balconyRoom);

        for (int floor = 1; floor <= 5; floor++) {
            for (int i = 1; i <= 5; i++) {
                String roomNumber = floor + String.format("%02d", i);
                RoomType type = types.get(random.nextInt(types.size()));
                String status = statuses[random.nextInt(statuses.length)];
                
                Room room = Room.builder()
                        .roomNumber(roomNumber)
                        .floorNumber(floor)
                        .roomType(type)
                        .status(status)
                        .build();
                roomRepository.save(room);
            }
        }

        // 3. Seed Services
        Service water = Service.builder().name("Nước suối").price(new BigDecimal("10000")).unit("Chai").build();
        Service cig = Service.builder().name("Thuốc lá").price(new BigDecimal("25000")).unit("Bao").build();
        Service towel = Service.builder().name("Thuê Khăn Tắm").price(new BigDecimal("20000")).unit("Cái").build();
        Service laundry = Service.builder().name("Giặt đồ").price(new BigDecimal("50000")).unit("Kg").build();
        
        serviceRepository.saveAll(Arrays.asList(water, cig, towel, laundry));

        // 4. Seed Guests
        Guest g1 = Guest.builder().fullName("Nguyễn Văn A").identityNumber("001201010101").phone("0901234567").build();
        Guest g2 = Guest.builder().fullName("Lê Thị B").identityNumber("001201010102").phone("0911234567").build();
        Guest g3 = Guest.builder().fullName("Trần Văn C").identityNumber("001201010103").phone("0921234567").build();
        Guest g4 = Guest.builder().fullName("Phạm Thị D").identityNumber("001201010104").phone("0931234567").build();
        Guest g5 = Guest.builder().fullName("Hoàng Văn E").identityNumber("001201010105").phone("0941234567").build();
        
        guestRepository.saveAll(Arrays.asList(g1, g2, g3, g4, g5));

        // 5. Seed Reservations
        List<Room> allRooms = roomRepository.findAll();
        
        // CHECKED_IN Reservation (Currently staying)
        Room occupiedRoom = allRooms.stream().filter(r -> r.getStatus().equals("OCCUPIED")).findFirst().orElse(allRooms.get(0));
        Reservation res1 = Reservation.builder()
                .guest(g1)
                .roomType(occupiedRoom.getRoomType())
                .room(occupiedRoom)
                .bookingReference("BKG" + System.currentTimeMillis())
                .checkInExpected(LocalDateTime.now().minusDays(1))
                .checkOutExpected(LocalDateTime.now().plusDays(1))
                .status("CHECKED_IN")
                .depositAmount(new BigDecimal("100000"))
                .build();
        reservationRepository.save(res1);

        // CHECKED_OUT Reservation (Stayed and left)
        Room availRoom = allRooms.stream().filter(r -> r.getStatus().equals("AVAILABLE")).findFirst().orElse(allRooms.get(1));
        Reservation res2 = Reservation.builder()
                .guest(g2)
                .roomType(availRoom.getRoomType())
                .room(availRoom)
                .bookingReference("BKG" + (System.currentTimeMillis() - 1000))
                .checkInExpected(LocalDateTime.now().minusDays(3))
                .checkOutExpected(LocalDateTime.now().minusDays(1))
                .status("CHECKED_OUT")
                .depositAmount(new BigDecimal("0"))
                .build();
        reservationRepository.save(res2);

        // PENDING Reservation (Coming soon)
        Reservation res3 = Reservation.builder()
                .guest(g3)
                .roomType(doubleRoom)
                .room(null) // Room not assigned yet
                .bookingReference("BKG" + (System.currentTimeMillis() + 1000))
                .checkInExpected(LocalDateTime.now().plusDays(2))
                .checkOutExpected(LocalDateTime.now().plusDays(4))
                .status("PENDING")
                .depositAmount(new BigDecimal("200000"))
                .build();
        reservationRepository.save(res3);

        // 6. Seed Invoice & Payment for CHECKED_OUT reservation to generate Revenue
        Invoice inv1 = Invoice.builder()
                .reservation(res2)
                .totalAmount(new BigDecimal("600000")) // e.g. 2 nights * 300k
                .status(InvoiceStatus.PAID)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        invoiceRepository.save(inv1);

        Payment pay1 = Payment.builder()
                .invoice(inv1)
                .amount(new BigDecimal("600000"))
                .paymentMethod(PaymentMethod.CASH)
                .paymentTime(LocalDateTime.now().minusDays(1))
                .paymentReference("CASH-" + System.currentTimeMillis())
                .build();
        paymentRepository.save(pay1);
        
        // Also a partial payment for the CHECKED_IN reservation
        Invoice inv2 = Invoice.builder()
                .reservation(res1)
                .totalAmount(new BigDecimal("400000")) 
                .status(InvoiceStatus.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();
        invoiceRepository.save(inv2);
        
        Payment pay2 = Payment.builder()
                .invoice(inv2)
                .amount(new BigDecimal("100000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentTime(LocalDateTime.now())
                .paymentReference("BANK-" + System.currentTimeMillis())
                .build();
        paymentRepository.save(pay2);
    }
}
