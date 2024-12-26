package com.example.auth_service.service;

import com.example.auth_service.api.v1.dto.Player;
import com.example.auth_service.api.v1.dto.PlayerDTORequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PlayerServiceClient {
    @Autowired
    private WebClient webClient;

    public void addPlayer(PlayerDTORequest player) {
        webClient.post()
                .uri("http://localhost:8094/playerPrivate/create")
                .body(Mono.just(player), PlayerDTORequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
