package com.example.auth_service.api.v1.controller;

import com.example.auth_service.api.v1.dto.AuthenticationRequest;
import com.example.auth_service.api.v1.dto.AuthenticationResponse;
import com.example.auth_service.api.v1.dto.RegisterRequest;
import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/authHelp")
@RequiredArgsConstructor
public class AuthHelpController {
    private final AuthService authService;


    @GetMapping("/getUserId")
    public ResponseEntity<Long> getUserId(@RequestHeader("Authorization") String authorizationHeader) {
        String jwt = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(authService.getUserId(jwt));
    }
}

