package com.bank.cms.service.impl;


import com.bank.cms.dto.request.LoginRequest;
import com.bank.cms.dto.request.RegisterRequest;
import com.bank.cms.dto.response.AuthResponse;
import com.bank.cms.entity.User;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.repository.CustomerRepository;
import com.bank.cms.repository.UserRepository;
import com.bank.cms.security.JwtService;
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

    public AuthResponse login(LoginRequest request){

        // 1. Checking for BAD Credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Generate fresh token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found exception"));

        return new AuthResponse(
            jwtService.generateAccessToken(user),
            jwtService.generateRefreshToken(user)
        );
    }
}
