package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.*;
import com.cybersport.room.api.v1.mapper.RoomMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
public class RoomService {

    @Autowired
    private RoomServiceData roomServiceData;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomWebSocketService roomWebSocketService;

    @Autowired
    private GameServiceClient gameServiceClient;

    @Autowired
    private RoomRedisService roomRedisService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<Long, ScheduledFuture<?>> roomTimers = new ConcurrentHashMap<>();
    private static final long ACCEPT_TIMEOUT_SECONDS = 30;

    public CreateRoomResponseDTO createRoom(Long creatorId, Integer minPlayers){
        return roomServiceData.createRoom(creatorId, minPlayers);
    }

    public List<RoomDTO> getAvailableRooms(){
        return roomServiceData.getAvailableRooms()
                .stream()
                .map(roomMapper::roomToRoomDTO)
                .collect(Collectors.toList());
    }

    public JoinRoomResponseDTO joinToRoom(Long playerId, Long roomId){
        JoinRoomResponseDTO answer = roomServiceData.joinToRoom(playerId, roomId);
        if (!answer.isError())
            roomWebSocketService.notifyPlayerJoined(roomId, playerId);
        return answer;
    }

    public LeaveRoomResponseDTO leaveFromRoom(Long playerId, Long roomId){
        LeaveRoomResponseDTO answer = roomServiceData.leaveFromRoom(playerId, roomId);
        if (!answer.isError()) {
            roomWebSocketService.notifyPlayerLeaved(roomId, playerId);
            roomRedisService.clearRoomData(roomId);
            if (answer.isNewCreator()) {
                roomWebSocketService.notifyPlayerLeaved(roomId, playerId);
                roomWebSocketService.notifyNewLeader(roomId, answer.getCreator());
            }
        }
        return answer;
    }

    public StartGameRoomResponse startGame(Long playerId, Long roomId) {
        StartGameRoomResponse answer = roomServiceData.startGame(playerId, roomId);
        if (!answer.isError()) {
            roomWebSocketService.notifyToAcceptGame(roomId);
            startRoomAcceptanceTimer(roomId);
        }
        return answer;
    }

    private void startRoomAcceptanceTimer(Long roomId) {
        // Останавливаем старый таймер, если он есть
        stopRoomAcceptanceTimer(roomId);

        // Запуск нового таймера для этой комнаты
        ScheduledFuture<?> timer = scheduler.schedule(() -> {
            if (!roomServiceData.getPlayersNotAccepted(roomId).isEmpty()) {
                timeOutedToAccept(roomId);
            }
        }, ACCEPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        roomTimers.put(roomId, timer);
    }

    private void stopRoomAcceptanceTimer(Long roomId) {
        ScheduledFuture<?> timer = roomTimers.get(roomId);
        if (timer != null && !timer.isDone()) {
            timer.cancel(true);
        }
    }

    public void timeOutedToAccept(Long roomId){
        roomWebSocketService.notifyToTimeOut(roomId);
        roomRedisService.clearRoomData(roomId);
        List<Long> players = roomServiceData.getPlayersNotAccepted(roomId);
        for (Long playerId : players) {
            leaveFromRoom(playerId, roomId);
        }
        roomTimers.remove(roomId);
    }


    @PreDestroy
    public void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public AcceptGameRoomDTO acceptGame(PlayerGameData playerGameData, Long roomId) {
        AcceptGameRoomDTO answer = roomServiceData.acceptGame(playerGameData.getPlayerId(), roomId);
        if (!answer.isError()) {
            roomRedisService.addPlayerGameInfo(roomId, playerGameData);
            if (!answer.isLast()) {
                roomWebSocketService.updateAcceptedPlayers(roomId, answer.getPlayerAcceptedCount());
            }
            else {
                roomWebSocketService.notifyToStartGame(roomId);
                int serverPort = gameServiceClient.createGameRoom(
                        RoomGameData.builder()
                                .roomId(roomId)
                                .players(roomRedisService.getAllPlayersGameData(roomId))
                                .build()
                );
                roomWebSocketService.sendServerPort(roomId, serverPort);

            }
        }
        return answer;
    }


}
