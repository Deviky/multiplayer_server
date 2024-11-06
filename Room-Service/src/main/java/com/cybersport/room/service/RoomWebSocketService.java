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

    public void notifyPlayerLeaved(Long roomId, Long playerId){
        Player player = playerServiceClient.getPlayerById(playerId);
        String message = "Игрок " + player.getNickname() + " вышел из комнаты";

        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);

    }

    public void notifyNewLeader(Long roomId, Long newLeaderId){
        Player newLeader= playerServiceClient.getPlayerById(newLeaderId);
        String message = "Новый лидер -  " + newLeader.getNickname();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
        String privateMessage = "Теперь вы новый лидер!";
        simpMessagingTemplate.convertAndSend("/topic/player/" + newLeaderId, privateMessage);
    }

    public void notifyToAcceptGame(Long roomId){
        String message = "Примите игру!";
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }


    public void updateAcceptedPlayers(Long roomId, Integer countNow){
        String message = "Игроков приняло - " + countNow;
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }

    public void notifyToStartGame(Long roomId){
        String message = "ИГРА НАЧИНАЕТСЯ!";
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }

    public void notifyToTimeOut(Long roomId){
        String message = "Время вышло(";
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }

}
