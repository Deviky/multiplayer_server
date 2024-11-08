package com.cybersport.room.api.v1.dto;

import com.cybersport.room.enums.WebSocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketRoomMessage {
    WebSocketMessageType Type;
    String message;
}
