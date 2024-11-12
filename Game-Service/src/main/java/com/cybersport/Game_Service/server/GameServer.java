package com.cybersport.Game_Service.server;


import com.cybersport.Game_Service.dto.Player;
import com.cybersport.Game_Service.dto.PlayerGameData;
import org.springframework.stereotype.Service;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class GameServer {

    private HashMap<Long, Player> playerHashMap;
    private DatagramSocket socket;

    public GameServer(List<Player> players, Integer serverPort) throws SocketException {
        this.playerHashMap = players.stream()
                .collect(Collectors.toMap(Player::getPlayerId, player -> player, (oldValue, newValue) -> newValue, HashMap::new));
        socket = new DatagramSocket(serverPort);
    }





}
