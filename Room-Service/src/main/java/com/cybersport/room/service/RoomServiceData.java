package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.Player;
import com.cybersport.room.entity.Room;
import com.cybersport.room.entity.RoomPlayer;
import com.cybersport.room.enums.PlayerStatus;
import com.cybersport.room.enums.PlayerTeam;
import com.cybersport.room.enums.RoomStatus;
import com.cybersport.room.repository.RoomPlayerRepository;
import com.cybersport.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceData {

    private final RoomRepository roomRepository;
    private final RoomPlayerRepository roomPlayerRepository;
    private final PlayerServiceClient playerServiceClient;


    @Transactional
    public String createRoom(Long creatorId, Integer minPlayers) {
        if (isPlayerInRoom(creatorId))
            return "ERROR: you are in another room now!";

        Player player = playerServiceClient.getPlayerById(creatorId);
        Room room = Room.builder()
                .creator(creatorId)
                .createdAt(LocalDateTime.now())
                .status(RoomStatus.WAITING)
                .lowElo(player.getElo() - 200)
                .highElo(player.getElo() + 200)
                .minPlayers(minPlayers)
                .build();

        RoomPlayer creatorPlayer = RoomPlayer.builder()
                .playerId(creatorId)
                .room(room)
                .playerTeam(PlayerTeam.NOTEAM)
                .playerStatus(PlayerStatus.WAITSTART)
                .build();

        creatorPlayer.setRoom(room);
        roomRepository.save(room);
        roomPlayerRepository.save(creatorPlayer);

        return "Room created " + room.getId();
    }


    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms(RoomStatus.WAITING, 6);
    }

    @Transactional
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
                .playerTeam(PlayerTeam.NOTEAM)
                .playerStatus(PlayerStatus.WAITSTART)
                .build();

        roomPlayerRepository.save(roomPlayer);
        room.getRoomPlayers().add(roomPlayer);
        roomRepository.save(room);

        return "You joined room " + roomId;
    }

    @Transactional
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
                if (room.getStatus().equals(RoomStatus.READY)){
                    room.setStatus(RoomStatus.WAITING);
                    updatePlayerStatusToWaitStart(roomId);
                }
                roomPlayerRepository.delete(playerToRemove);
                roomRepository.save(room);

                return "New leader - " + newCreator.getPlayerId();
            }
        } else {
            roomPlayers.remove(playerToRemove);
            if (room.getStatus().equals(RoomStatus.READY)){
                room.setStatus(RoomStatus.WAITING);
                updatePlayerStatusToWaitStart(roomId);
            }
            roomPlayerRepository.delete(playerToRemove);
            room.setRoomPlayers(roomPlayers);
            roomRepository.save(room);
            return "You left the room.";
        }
    }
    @Transactional
    private boolean isPlayerInRoom(Long playerId) {
        List<Room> rooms = roomRepository.findRoomsByPlayerId(playerId);
        return !rooms.isEmpty();
    }

    @Transactional
    public String startGame(Long playerId, Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty())
            return "ERROR: FATAL!";
        Room room = roomOpt.get();
        if (!room.getCreator().equals(playerId))
            return "ERROR: you're not a leader!";
        if (room.getRoomPlayers().size() < room.getMinPlayers())
            return "ERROR: not enough players to start the game";
        updatePlayerStatusToNotAccepted(roomId);
        room.setStatus(RoomStatus.READY);
        roomRepository.save(room);
        return "WAIT FOR ACCEPT!";
    }

    @Transactional
    public String acceptGame(Long playerId, Long roomId){
        RoomPlayer roomPlayer = roomPlayerRepository.findByPlayerId(playerId);
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty())
            return "ERROR: FATAL!";
        Room room = roomOpt.get();
        if (roomPlayer == null)
            return "ERROR: FATAL!";
        if (!roomPlayer.getRoom().getId().equals(roomId))
            return "ERROR: you're not in this room!";
        if (!roomPlayer.getPlayerStatus().equals(PlayerStatus.NOTACCEPTED))
            return "ERROR: you have another status";
        roomPlayer.setPlayerStatus(PlayerStatus.ACCEPTED);
        roomPlayerRepository.save(roomPlayer);
        int playersAcceptedCount = getAcceptedPlayersCount(roomId);
        if (playersAcceptedCount == room.getMinPlayers()){
            room.setStatus(RoomStatus.INGAME);
            roomRepository.save(room);
            return "START GAME!";
        }
        return "You accept! Players accepted - " + playersAcceptedCount;
    }

    private int getAcceptedPlayersCount(Long roomId) {
        List<RoomPlayer> roomPlayers = roomPlayerRepository.findByRoomId(roomId);
        return (int) roomPlayers.stream()
                .filter(player -> player.getPlayerStatus() == PlayerStatus.ACCEPTED)
                .count();
    }

    @Transactional
    private void updatePlayerStatusToNotAccepted(Long roomId) {
        List<RoomPlayer> roomPlayers = roomPlayerRepository.findByRoomId(roomId);

        roomPlayers.forEach(roomPlayer -> roomPlayer.setPlayerStatus(PlayerStatus.NOTACCEPTED));

        roomPlayerRepository.saveAll(roomPlayers);
    }

    @Transactional
    private void updatePlayerStatusToWaitStart(Long roomId) {
        List<RoomPlayer> roomPlayers = roomPlayerRepository.findByRoomId(roomId);

        roomPlayers.forEach(roomPlayer -> roomPlayer.setPlayerStatus(PlayerStatus.WAITSTART));

        roomPlayerRepository.saveAll(roomPlayers);
    }


    public List<Long> getPlayersNotAccepted(Long roomId){
        List<RoomPlayer> players = roomPlayerRepository.findByRoomId(roomId);
        return players.stream()
                .filter(player -> player.getPlayerStatus() == PlayerStatus.NOTACCEPTED)
                .map(RoomPlayer::getPlayerId)
                .collect(Collectors.toList());
    }





}
