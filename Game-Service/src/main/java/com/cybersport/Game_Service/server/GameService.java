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

    public GameServer createServer(RoomGameData roomGameData) {
        // Преобразуем List<PlayerGameData> в List<Player>
        List<Player> players = roomGameData.getPlayers().stream()
                .map(this::convertToPlayer)
                .collect(Collectors.toList());

        // Генерация или получение IP-адреса для сервера (например, с помощью InetAddress.getLocalHost())
        InetAddress serverInetAddress = getLocalIpAddress();

        // Получаем свободный порт для сервера
        Integer serverPort = getFreePort();

        // Создаем GameServer с автоматически полученным IP-адресом и портом
        return new GameServer(roomGameData.getRoomId(), players, serverInetAddress, serverPort);
    }

    // Метод для конвертации одного PlayerGameData в Player
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

    // Метод для получения локального IP-адреса сервера
    private InetAddress getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost();  // Получаем IP-адрес текущего устройства
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to determine local IP address.", e);
        }
    }

    // Метод для получения свободного порта
    private Integer getFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Unable to find a free port.", e);
        }
    }
}
