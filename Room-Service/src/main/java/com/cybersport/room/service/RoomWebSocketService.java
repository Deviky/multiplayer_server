package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.Player;
import com.cybersport.room.api.v1.dto.WebSocketRoomMessage;
import com.cybersport.room.enums.WebSocketMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RoomWebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PlayerServiceClient playerServiceClient;


    public void notifyPlayerJoined(Long roomId, Long playerId){
        Player player = playerServiceClient.getPlayerById(playerId);
        String message = "Игрок " + player.getNickname() + " присоединился к комнате";
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.PLAYER_JOINED)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);

    }

    public void notifyPlayerLeaved(Long roomId, Long playerId){
        Player player = playerServiceClient.getPlayerById(playerId);
        String message = "Игрок " + player.getNickname() + " вышел из комнаты";
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.PLAYER_LEAVED)
                .message(message)
                .build();

        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);

    }

    public void notifyNewLeader(Long roomId, Long newLeaderId){
        Player newLeader= playerServiceClient.getPlayerById(newLeaderId);
        String message = "Новый лидер -  " + newLeader.getNickname();
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.NEW_ROOM_LEADER)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage );
        String privateMessage = "Теперь вы новый лидер!";
        WebSocketRoomMessage wsPrivateMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.YOU_NEW_LEADER)
                .message(privateMessage)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/player/" + newLeaderId, wsPrivateMessage);
    }

    public void notifyToAcceptGame(Long roomId){
        String message = "Примите игру!";
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.LEADER_STARTS_GAME)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
    }


    public void updateAcceptedPlayers(Long roomId, Integer countNow){
        String message = "Игроков приняло - " + countNow;
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.PLAYER_ACCEPT_GAME)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
    }

    public void notifyToStartGame(Long roomId){
        String message = "ИГРА НАЧИНАЕТСЯ!";
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.START_CONNECTION)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
    }

    public void notifyToTimeOut(Long roomId){
        String message = "Время вышло(";
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.TIME_OUT)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
    }

    public void sendServerPort(Long roomId, Integer serverPort) {
        String message = serverPort.toString();
        WebSocketRoomMessage wsMessage = WebSocketRoomMessage.builder()
                .Type(WebSocketMessageType.SERVER_PORT)
                .message(message)
                .build();
        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
    }
}
