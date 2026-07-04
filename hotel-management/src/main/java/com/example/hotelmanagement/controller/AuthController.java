package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.request.LoginRequest;
import com.example.hotelmanagement.dto.response.ApiResponse;
import com.example.hotelmanagement.dto.response.UserResponse;
import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.security.custom.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final com.example.hotelmanagement.security.jwt.JwtTokenProvider tokenProvider;
    private final com.example.hotelmanagement.repository.UserRepository userRepository;
    private final com.example.hotelmanagement.repository.RoleRepository roleRepository;
    private final com.example.hotelmanagement.repository.GuestRepository guestRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.example.hotelmanagement.dto.response.JwtAuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roleName(user.getRole().getName())
                .build();

        com.example.hotelmanagement.dto.response.JwtAuthResponse jwtAuthResponse = com.example.hotelmanagement.dto.response.JwtAuthResponse.builder()
                .accessToken(token)
                .user(userResponse)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtAuthResponse));
    }

    @PostMapping("/register-customer")
    public ResponseEntity<ApiResponse<String>> registerCustomer(@Valid @RequestBody com.example.hotelmanagement.dto.request.RegisterCustomerRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder().code(400).message("Username already exists").build());
        }

        com.example.hotelmanagement.entity.Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(customerRole)
                .isActive(true)
                .build();
        userRepository.save(newUser);

        // Map to Guest or create new Guest
        com.example.hotelmanagement.entity.Guest guest = guestRepository.findByIdentityNumber(request.getIdentityNumber()).orElse(null);
        if (guest == null) {
            guest = com.example.hotelmanagement.entity.Guest.builder()
                    .fullName(request.getFullName())
                    .identityNumber(request.getIdentityNumber())
                    .phone(request.getPhone())
                    .user(newUser)
                    .build();
        } else {
            // Update existing guest with User link
            guest.setUser(newUser);
        }
        guestRepository.save(guest);

        return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.<UserResponse>builder().code(401).message("Unauthorized").build());
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roleName(user.getRole().getName())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
