package com.cybersport.room.service;


import com.cybersport.room.api.v1.dto.Player;
import com.cybersport.room.api.v1.dto.PlayerGameData;
import com.cybersport.room.api.v1.dto.RoomGameData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GameServiceClient {

    @Autowired
    private WebClient webClient;

    public Integer createGameRoom(RoomGameData roomGameData) {
        return webClient.post()
                .uri("http://localhost:8096/game/start")
                .body(Mono.just(roomGameData), RoomGameData.class)
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
    }

}
