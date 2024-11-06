package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.RoomDTO;
import com.cybersport.room.api.v1.mapper.RoomMapper;
import com.cybersport.room.entity.Room;
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ConcurrentMap<Long, ScheduledFuture<?>> roomTimers = new ConcurrentHashMap<>();
    private static final long ACCEPT_TIMEOUT_SECONDS = 30;

    public String createRoom(Long creatorId, Integer minPlayers){
        return roomServiceData.createRoom(creatorId, minPlayers);
    }

    public List<RoomDTO> getAvailableRooms(){
        return roomServiceData.getAvailableRooms()
                .stream()
                .map(roomMapper::roomToRoomDTO)
                .collect(Collectors.toList());
    }

    public String joinToRoom(Long playerId, Long roomId){
        String answer = roomServiceData.joinToRoom(playerId, roomId);
        if (!answer.startsWith("ERROR"))
            roomWebSocketService.notifyPlayerJoined(roomId, playerId);
        return answer;
    }

    public String leaveFromRoom(Long playerId, Long roomId){
        String answer = roomServiceData.leaveFromRoom(playerId, roomId);
        if (!answer.startsWith("ERROR")) {
            roomWebSocketService.notifyPlayerLeaved(roomId, playerId);
            if (answer.startsWith("New leader - ")) {
                String[] parts = answer.split(" - ");
                if (parts.length == 2) {
                    Long newLeaderId = Long.parseLong(parts[1]);
                    roomWebSocketService.notifyPlayerLeaved(roomId, playerId);
                    roomWebSocketService.notifyNewLeader(roomId, newLeaderId);
                }
            }
        }
        return answer;
    }

    public String startGame(Long playerId, Long roomId) {
        String answer = roomServiceData.startGame(playerId, roomId);
        if (!answer.startsWith("ERROR")) {
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
        List<Long> players = roomServiceData.getPlayersNotAccepted(roomId);
        for (Long playerId : players)
            leaveFromRoom(playerId, roomId);
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

    public String acceptGame(Long playerId, Long roomId) {
        String answer = roomServiceData.acceptGame(playerId, roomId);
        if (!answer.startsWith("ERROR")) {
            if (answer.startsWith("You accept!")) {
                String[] parts = answer.split(" - ");
                if (parts.length == 2) {
                    Integer countNow = Integer.parseInt(parts[1]);
                    roomWebSocketService.updateAcceptedPlayers(roomId, countNow);
                }
            }
            else if (answer.startsWith("START GAME!")){
                roomWebSocketService.notifyToStartGame(roomId);
            }
        }
        return answer;
    }


}
