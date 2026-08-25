package com.appstore.backend.service;

import com.appstore.backend.dtos.LoginRequest;
import com.appstore.backend.dtos.LoginResponse;
import com.appstore.backend.dtos.RegisterRequest;
import com.appstore.backend.dtos.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RegisterResponse registrar(RegisterRequest request);
}
