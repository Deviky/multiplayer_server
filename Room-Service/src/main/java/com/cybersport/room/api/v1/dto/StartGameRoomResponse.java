package com.cybersport.room.api.v1.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartGameRoomResponse {
    boolean isError;
    String message;

}