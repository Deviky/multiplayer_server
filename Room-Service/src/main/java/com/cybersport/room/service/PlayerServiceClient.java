package com.cybersport.room.service;


import com.cybersport.room.api.v1.dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PlayerServiceClient {
    @Autowired
    private WebClient webClient;


    public Player getPlayerById(Long id){
        return webClient.get()
                .uri("http://localhost:8094/player/"+id)
                .retrieve()
                .bodyToMono(Player.class)
                .block();
    }
}
