package com.example.demo.services;

import com.example.demo.dtos.auth.AuthenticationRequest;
import com.example.demo.dtos.auth.AuthenticationResponse;
import com.example.demo.dtos.auth.RegisterRequest;

public interface AuthService {

    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
