package com.bank.cms.service.impl;

import com.bank.cms.dto.request.LoginRequest;
import com.bank.cms.dto.request.RegisterRequest;
import com.bank.cms.dto.response.AuthResponse;
import com.bank.cms.entity.User;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.exception.TooManyAttemptsException;
import com.bank.cms.rabbitmq.OtpMessage;
import com.bank.cms.rabbitmq.RabbitMQProducer;
import com.bank.cms.repository.CustomerRepository;
import com.bank.cms.repository.UserRepository;
import com.bank.cms.security.JwtService;
import com.bank.cms.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final RabbitMQProducer rabbitMQProducer;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        customerRepository.findCustomerByCifNumber(request.getCifNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: " + request.getCifNumber()));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCifNumber(request.getCifNumber());
        user.setRole(User.Role.CUSTOMER);
        userRepository.save(user);

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    public AuthResponse login(LoginRequest request) {

        // 1. Check if account is locked
        if (redisService.isLoginLocked(request.getEmail())) {
            throw new TooManyAttemptsException(
                    "Account locked due to too many failed attempts. Try again in 15 minutes.");
        }

        try {
            // 2. Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // 3. Success — reset failed attempts
            redisService.resetLoginAttempts(request.getEmail());

        } catch (BadCredentialsException e) {   // ✅ catch specific exception

            // 4. Failed — increment counter
            int attempts = redisService.incrementLoginAttempts(request.getEmail());

            if (attempts >= 5) {
                redisService.lockLogin(request.getEmail());
                throw new TooManyAttemptsException(
                        "Too many failed attempts. Account locked for 15 minutes.");
            }

            throw new TooManyAttemptsException(
                    "Invalid credentials. " + (5 - attempts) + " attempts remaining.");

        }   // ✅ catch block closes here

        // 5. Generate tokens — OUTSIDE the try-catch
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }   // ✅ method closes here

    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No token provided");
        }

        String token = authHeader.substring(7);

        long remainingTtl = jwtService.extractExpiration(token).getTime()
                - System.currentTimeMillis();

        if (remainingTtl > 0) {
            redisService.blacklistToken(token, remainingTtl / 1000);
        }

        return "Logged out successfully";
    }

    public String sendOtp(String email) {

        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Generate 6-digit OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        // 3. Store OTP in Redis with 5 min expiry
        redisService.set("otp:" + email, otp, 300);

        // 4. Push to RabbitMQ — SMS worker will pick it up
        rabbitMQProducer.sendOtp(new OtpMessage(
                "9876543210",    // in production: user.getMobileNumber()
                email,
                otp,
                "LOGIN"
        ));

        return "OTP sent to registered mobile number";
    }
}