package com.bank.cms.service.impl;


import com.bank.cms.dto.request.LoginRequest;
import com.bank.cms.dto.request.RegisterRequest;
import com.bank.cms.dto.response.AuthResponse;
import com.bank.cms.entity.User;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.repository.CustomerRepository;
import com.bank.cms.repository.UserRepository;
import com.bank.cms.security.JwtService;
import com.bank.cms.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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

    public AuthResponse register(RegisterRequest request){

        // 1. Check email is taken or NOT -- if taken -> error | else -> success
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("User not exits");
        }

        // 2. Check the customer number actually exists in your customer details
        customerRepository.findCustomerByCifNumber(request.getCifNumber())
                .orElseThrow(() ->  new ResourceNotFoundException(
                        "Account Not found " + request.getCifNumber()));

        // 3. New User is created , store the password in Bcrypt
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCifNumber(request.getCifNumber());
        user.setRole(User.Role.CUSTOMER);

        userRepository.save(user);

        // 4. Issue token immediately issued after registration
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }
    public AuthResponse login(LoginRequest request) {

        // 1. Check if locked
        if (redisService.isLoginLocked(request.getEmail())) {
            throw new RuntimeException(
                    "Account locked due to too many failed attempts. Try again in 15 minutes.");
        }

        try {
            // 2. Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // 3. Success — reset attempts
            redisService.resetLoginAttempts(request.getEmail());

        } catch (Exception e) {

            // 4. Failed — increment counter
            int attempts = redisService.incrementLoginAttempts(request.getEmail());

            if (attempts >= 5) {
                redisService.lockLogin(request.getEmail());
                throw new RuntimeException(
                        "Too many failed attempts. Account locked for 15 minutes.");
            }

            throw new RuntimeException(
                    "Invalid credentials. " + (5 - attempts) + " attempts remaining.");
        }

        // 5. Generate tokens
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }


    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No token provided");
        }

        String token = authHeader.substring(7);

        // Get remaining TTL so blacklist auto-expires with the token
        long remainingTtl = jwtService.extractExpiration(token).getTime()
                - System.currentTimeMillis();

        if (remainingTtl > 0) {
            redisService.blacklistToken(token, remainingTtl / 1000);
        }

        return "Logged out successfully";
    }


}
