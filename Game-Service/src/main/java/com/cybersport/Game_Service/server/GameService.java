package com.cybersport.Game_Service.server;


import com.cybersport.Game_Service.dto.Player;
import com.cybersport.Game_Service.dto.PlayerGameData;
import com.cybersport.Game_Service.dto.RoomGameData;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    public Integer createServer(RoomGameData roomGameData) {
        List<Player> players = roomGameData.getPlayers().stream()
                .map(this::convertToPlayer)
                .collect(Collectors.toList());
        Integer serverPort = getFreePort();


        return serverPort;

    }

    private Player convertToPlayer(PlayerGameData playerGameData) {
        try {
            return Player.builder()
                    .playerId(playerGameData.getPlayerId())
                    .IpAddress(InetAddress.getByName(playerGameData.getIpAddress()))
                    .port(playerGameData.getPort())
                    .build();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid IP address: " + playerGameData.getIpAddress(), e);
        }
    }


    private Integer getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Unable to find a free port.", e);
        }
    }
}
