package com.cybersport.room.components;

import com.cybersport.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private RoomService roomService;


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

        MessageHeaderAccessor headerAccessor =
                MessageHeaderAccessor.getAccessor(event.getMessage().getHeaders(), MessageHeaderAccessor.class);

        StompHeaderAccessor stompHeaderAccessor =
                MessageHeaderAccessor.getAccessor((Message<?>) headerAccessor.getHeader("simpConnectMessage"), StompHeaderAccessor.class);

        String playerId = stompHeaderAccessor.getNativeHeader("playerId") != null ? stompHeaderAccessor.getNativeHeader("playerId").get(0) : null;
        String roomId = stompHeaderAccessor.getNativeHeader("roomId") != null ? stompHeaderAccessor.getNativeHeader("roomId").get(0) : null;


        Long playerIdLong = (playerId != null) ? Long.valueOf(playerId) : null;
        Long roomIdLong = (roomId != null) ? Long.valueOf(roomId) : null;

        if (playerIdLong != null && roomIdLong != null) {
            stompHeaderAccessor.getSessionAttributes().put("playerId", playerIdLong);
            stompHeaderAccessor.getSessionAttributes().put("roomId", roomIdLong);
        }
        else
            System.err.println("Player ID or Room ID is missing in WebSocket headers");

    }




    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Long playerId = (Long) headerAccessor.getSessionAttributes().get("playerId");
        Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");

        if (playerId != null && roomId != null) {
            roomService.leaveFromRoom(playerId, roomId);
        }
    }
}
