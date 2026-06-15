package com.bank.cms.controller;

import com.bank.cms.api.response.ApiResponse;
import com.bank.cms.dto.request.LoginRequest;
import com.bank.cms.dto.request.RegisterRequest;
import com.bank.cms.dto.response.AuthResponse;
import com.bank.cms.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    public final AuthServiceImpl authService;

    // 1. Register
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return new ApiResponse<>(
                "Success",
                "Logged in Successfully",
                authService.register(request)
        );
    }

    // 2. Login
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return new ApiResponse<>(
                "Success",
                "Logged in Successfully",
                authService.login(request)
        );
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String authHeader) {
        return new ApiResponse<>(
                "SUCCESS",
                "Logged out",
                authService.logout(authHeader)
        );
    }

}
