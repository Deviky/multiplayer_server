package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomWebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PlayerServiceClient playerServiceClient;

    public void notifyPlayerJoined(Long roomId, Long playerId){
        Player player = playerServiceClient.getPlayerById(playerId);
        String message = "Игрок " + player.getNickname() + " присоединился к комнате";

        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);

    }


}
