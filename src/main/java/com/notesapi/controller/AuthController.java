package com.notesapi.controller;

import com.notesapi.dto.AuthResponse;
import com.notesapi.dto.LoginRequest;
import com.notesapi.dto.RegisterRequest;
import com.notesapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse registerResponse = authService.register(registerRequest);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

}
