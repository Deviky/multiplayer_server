package com.example.auth_service.service;


import com.example.auth_service.api.v1.dto.AuthenticationRequest;
import com.example.auth_service.api.v1.dto.AuthenticationResponse;
import com.example.auth_service.api.v1.dto.PlayerDTORequest;
import com.example.auth_service.api.v1.dto.RegisterRequest;
import com.example.auth_service.entity.Role;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PlayerServiceClient playerServiceClient;

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<User> userDemo = repository.findByNickname(request.getNickname());
        if (userDemo.isPresent())
            return AuthenticationResponse.builder()
                    .isError(true)
                    .message("Пользователь с таким ником уже существует")
                    .token("")
                    .build();

        var user = User.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        repository.save(user);
        playerServiceClient.addPlayer(PlayerDTORequest
                .builder()
                .id(user.getId())
                .nickname(request.getNickname())
                .build());

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .isError(false)
                .message("Регистрация прошла успешно!")
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNickname(),
                        request.getPassword()
                )
        );
        var user = repository.findByNickname(request.getNickname()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public Long getUserId(String jwt){
        Optional<User> user = repository.findByNickname(jwtService.extractUsername(jwt));
        return user.map(User::getId).orElse(null);
    }
}
