package com.customworld.service;

import com.customworld.dto.request.LoginRequest;
import com.customworld.dto.request.RegisterRequest;
import com.customworld.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
}