package com.cybersport.Game_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    Long playerId;
    InetAddress IpAddress;
    Integer port;
}