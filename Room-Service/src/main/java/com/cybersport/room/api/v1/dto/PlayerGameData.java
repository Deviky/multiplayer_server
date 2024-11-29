package com.cybersport.room.api.v1.dto;


import com.cybersport.room.enums.PlayerTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerGameData {
    Long playerId;
    PlayerTeam team;
    String IpAddress;
    Integer port;
}
