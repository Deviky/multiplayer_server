package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.Player;
import com.cybersport.room.entity.Room;
import com.cybersport.room.entity.RoomPlayer;
import com.cybersport.room.entity.RoomStatus;
import com.cybersport.room.repository.RoomPlayerRepository;
import com.cybersport.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomServiceData {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final PlayerServiceClient playerServiceClient;

    public String createRoom(Long creatorId) {
        if (isPlayerInRoom(creatorId))
            return "ERROR: you are in another room now!";

        Player player = playerServiceClient.getPlayerById(creatorId);
        Room room = Room.builder()
                .creator(creatorId)
                .createdAt(LocalDateTime.now())
                .status(RoomStatus.WAITING)
                .lowElo(player.getElo() - 200)
                .highElo(player.getElo() + 200)
                .build();

        RoomPlayer creatorPlayer = RoomPlayer.builder()
                .playerId(creatorId)
                .room(room)
                .build();

        creatorPlayer.setRoom(room);
        roomRepository.save(room);
        roomPlayerRepository.save(creatorPlayer);

        return "Room created " + room.getId();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms(RoomStatus.WAITING, 6);
    }

    public String joinToRoom(Long playerId, Long roomId) {
        if (isPlayerInRoom(playerId))
            return "ERROR: you are in another room now!";

        Player player = playerServiceClient.getPlayerById(playerId);
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (player == null || roomOpt.isEmpty())
            return "ERROR: FATAL!!";

        Room room = roomOpt.get();
        if (room.getRoomPlayers().size() >= 6)
            return "ERROR: Room overflow!";
        if (room.getStatus() != RoomStatus.WAITING)
            return "ERROR: Room is not available now!";

        int playerElo = player.getElo();
        if (playerElo <= room.getLowElo() || playerElo >= room.getHighElo())
            return "ERROR: You can't join with your elo!";

        RoomPlayer roomPlayer = RoomPlayer.builder()
                .playerId(playerId)
                .room(room)
                .build();

        roomPlayerRepository.save(roomPlayer);
        room.getRoomPlayers().add(roomPlayer);
        roomRepository.save(room);

        return "You joined room " + roomId;
    }

    public String leaveFromRoom(Long playerId, Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return "ERROR: FATAL!";
        }

        Room room = roomOpt.get();
        List<RoomPlayer> roomPlayers = room.getRoomPlayers();


        RoomPlayer playerToRemove = roomPlayers.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);

        if (playerToRemove == null) {
            return "ERROR: Player not found in the room.";
        }

        if (playerId.equals(room.getCreator())) {
            if (roomPlayers.size() <= 1) {
                roomRepository.deleteById(roomId);
                return "You left the room, and the room was destroyed.";
            } else {
                roomPlayers.remove(playerToRemove);

                Random random = new Random();
                int randomIndex = random.nextInt(roomPlayers.size());
                RoomPlayer newCreator = roomPlayers.get(randomIndex);

                room.setCreator(newCreator.getPlayerId());
                room.setRoomPlayers(roomPlayers);
                roomPlayerRepository.delete(playerToRemove);
                roomRepository.save(room);

                return "You left the room. New creator is player with ID: " + newCreator.getPlayerId();
            }
        } else {
            roomPlayers.remove(playerToRemove);
            roomPlayerRepository.delete(playerToRemove);
            room.setRoomPlayers(roomPlayers);
            roomRepository.save(room);
            return "You left the room.";
        }
    }

    private boolean isPlayerInRoom(Long playerId) {
        List<Room> rooms = roomRepository.findRoomsByPlayerId(playerId);
        return !rooms.isEmpty();
    }
}
