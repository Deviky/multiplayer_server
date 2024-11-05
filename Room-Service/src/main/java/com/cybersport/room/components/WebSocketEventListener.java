package com.cybersport.room.components;

import com.cybersport.room.service.RoomService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private final RoomService roomService;

    public WebSocketEventListener(RoomService roomService) {
        this.roomService = roomService;
    }



    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Получаем MessageHeaderAccessor из заголовков события
        MessageHeaderAccessor headerAccessor =
                MessageHeaderAccessor.getAccessor(event.getMessage().getHeaders(), MessageHeaderAccessor.class);

        // Получаем StompHeaderAccessor из заголовков
        StompHeaderAccessor stompHeaderAccessor =
                MessageHeaderAccessor.getAccessor((Message<?>) headerAccessor.getHeader("simpConnectMessage"), StompHeaderAccessor.class);

        // Получаем все заголовки
        System.out.println("Headers: " + stompHeaderAccessor.getMessageHeaders());

        // Извлекаем playerId и roomId из nativeHeaders
        String playerId = stompHeaderAccessor.getNativeHeader("playerId") != null ? stompHeaderAccessor.getNativeHeader("playerId").get(0) : null;
        String roomId = stompHeaderAccessor.getNativeHeader("roomId") != null ? stompHeaderAccessor.getNativeHeader("roomId").get(0) : null;

        // Отладочные сообщения
        System.out.println("Extracted playerId: " + playerId);
        System.out.println("Extracted roomId: " + roomId);

        // Парсим значения из заголовков, если они существуют
        Long playerIdLong = (playerId != null) ? Long.valueOf(playerId) : null;
        Long roomIdLong = (roomId != null) ? Long.valueOf(roomId) : null;

        // Проверяем, что playerId и roomId не равны null
        if (playerIdLong != null && roomIdLong != null) {
            // Сохраняем атрибуты в сессии
            stompHeaderAccessor.getSessionAttributes().put("playerId", playerIdLong);
            stompHeaderAccessor.getSessionAttributes().put("roomId", roomIdLong);
        } else {
            // Обработка случая, когда playerId или roomId не были переданы
            System.err.println("Player ID or Room ID is missing in WebSocket headers");
        }
    }




    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long playerId = (Long) headerAccessor.getSessionAttributes().get("playerId");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");

        if (playerId != null && roomId != null) {
            roomService.leaveFromRoom(playerId, roomId); // Удаление игрока из комнаты
        }
    }
}
