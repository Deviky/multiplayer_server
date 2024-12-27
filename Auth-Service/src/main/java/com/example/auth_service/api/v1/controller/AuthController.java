package com.example.auth_service.api.v1.controller;

import com.example.auth_service.service.AuthService;
import com.example.auth_service.api.v1.dto.AuthenticationRequest;
import com.example.auth_service.api.v1.dto.AuthenticationResponse;
import com.example.auth_service.api.v1.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        AuthenticationResponse response = authService.register(request);
        if (!response.getIsError())
            return ResponseEntity.ok(response);
        else
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authService.authenticate(request));
    }

}
