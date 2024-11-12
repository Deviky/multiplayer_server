package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.*;
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
    public CreateRoomResponseDTO createRoom(Long creatorId, Integer minPlayers) {
        if (isPlayerInRoom(creatorId)){
            return CreateRoomResponseDTO.builder()
                    .roomId(null)
                    .message("ERROR: you are in another room now!")
                    .build();
        }

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

        return CreateRoomResponseDTO.builder()
                .roomId(room.getId())
                .message("Room created")
                .build();
    }


    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms(RoomStatus.WAITING, 6);
    }

    @Transactional
    public JoinRoomResponseDTO joinToRoom(Long playerId, Long roomId) {
        Player player = playerServiceClient.getPlayerById(playerId);
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (player == null || roomOpt.isEmpty()) {
            return JoinRoomResponseDTO.builder()
                    .isError(true)
                    .message("FATAL ERROR!")
                    .roomInfo(null)
                    .build();
        }

        if (isPlayerInRoom(playerId)){
            return JoinRoomResponseDTO.builder()
                    .isError(true)
                    .message("ERROR: You are in another room!")
                    .roomInfo(null)
                    .build();
        }

        Room room = roomOpt.get();
        if (room.getRoomPlayers().size() >= 6) {
            return JoinRoomResponseDTO.builder()
                    .isError(true)
                    .message("ERROR: room is crowded!")
                    .roomInfo(null)
                    .build();
        }
        if (room.getStatus() != RoomStatus.WAITING) {
            return JoinRoomResponseDTO.builder()
                    .isError(true)
                    .message("ERROR: room is not available now!")
                    .roomInfo(null)
                    .build();
        }
        int playerElo = player.getElo();
        if (playerElo <= room.getLowElo() || playerElo >= room.getHighElo()) {
            return JoinRoomResponseDTO.builder()
                    .isError(true)
                    .message("ERROR: you can't join to this room with your elo!")
                    .roomInfo(null)
                    .build();
        }

        RoomPlayer roomPlayer = RoomPlayer.builder()
                .playerId(playerId)
                .room(room)
                .playerTeam(PlayerTeam.NOTEAM)
                .playerStatus(PlayerStatus.WAITSTART)
                .build();

        roomPlayerRepository.save(roomPlayer);
        room.getRoomPlayers().add(roomPlayer);
        roomRepository.save(room);

        return JoinRoomResponseDTO.builder()
                .isError(false)
                .message("You have joined!")
                .roomInfo(getRoomInfo(room))
                .build();
    }


    private RoomInfoDTO getRoomInfo(Room room){
        List<RoomPlayer> roomPlayers = room.getRoomPlayers();

        List<Player> players = roomPlayers.stream()
                .map(roomPlayer -> playerServiceClient.getPlayerById(roomPlayer.getPlayerId()))
                .collect(Collectors.toList());

        return RoomInfoDTO.builder()
                .id(room.getId())
                .players(players)
                .creator(room.getCreator())
                .status(room.getStatus())
                .lowElo(room.getLowElo())
                .highElo(room.getHighElo())
                .createdAt(room.getCreatedAt())
                .build();

    }


    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId).orElse(null);
    }

    @Transactional
    public LeaveRoomResponseDTO leaveFromRoom(Long playerId, Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return LeaveRoomResponseDTO.builder()
                    .isError(true)
                    .message("FATAL ERROR")
                    .isNewCreator(false)
                    .creator(null)
                    .build();
        }

        Room room = roomOpt.get();
        List<RoomPlayer> roomPlayers = room.getRoomPlayers();


        RoomPlayer playerToRemove = roomPlayers.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);

        if (playerToRemove == null) {
            return LeaveRoomResponseDTO.builder()
                    .isError(true)
                    .message("ERROR: Player not found in the room.")
                    .isNewCreator(false)
                    .creator(null)
                    .build();
        }

        if (playerId.equals(room.getCreator())) {
            if (roomPlayers.size() <= 1) {
                roomRepository.deleteById(roomId);
                return LeaveRoomResponseDTO.builder()
                        .isError(false)
                        .message("You left the room, and the room was destroyed.")
                        .isNewCreator(false)
                        .creator(null)
                        .build();
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

                return LeaveRoomResponseDTO.builder()
                        .isError(false)
                        .message("You left the room!")
                        .isNewCreator(true)
                        .creator(newCreator.getPlayerId())
                        .build();
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
            return LeaveRoomResponseDTO.builder()
                    .isError(false)
                    .message("You left the room!")
                    .isNewCreator(false)
                    .creator(null)
                    .build();
        }
    }
    @Transactional
    private boolean isPlayerInRoom(Long playerId) {
        List<Room> rooms = roomRepository.findRoomsByPlayerId(playerId);
        return !rooms.isEmpty();
    }

    @Transactional
    public StartGameRoomResponse startGame(Long playerId, Long roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty())
            return StartGameRoomResponse.builder()
                    .isError(true)
                    .message("ERROR: FATAL!")
                    .build();
        Room room = roomOpt.get();
        if (!room.getCreator().equals(playerId))
            return StartGameRoomResponse.builder()
                    .isError(true)
                    .message("ERROR: you're not a leader!")
                    .build();
        if (room.getRoomPlayers().size() < room.getMinPlayers())
            return StartGameRoomResponse.builder()
                    .isError(true)
                    .message("ERROR: not enough players to start the game")
                    .build();
        updatePlayerStatusToNotAccepted(roomId);
        room.setStatus(RoomStatus.READY);
        roomRepository.save(room);
        return StartGameRoomResponse.builder()
                .isError(false)
                .message("WAIT FOR ACCEPT!")
                .build();
    }

    @Transactional
    public AcceptGameRoomDTO acceptGame(Long playerId, Long roomId){
        RoomPlayer roomPlayer = roomPlayerRepository.findByPlayerId(playerId);
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty())
            return AcceptGameRoomDTO.builder()
                    .isError(true)
                    .isLast(false)
                    .message("ERROR: FATAL!")
                    .playerAcceptedCount(null)
                    .build();
        Room room = roomOpt.get();
        if (roomPlayer == null)
            return AcceptGameRoomDTO.builder()
                    .isError(true)
                    .isLast(false)
                    .message("ERROR: FATAL!")
                    .playerAcceptedCount(null)
                    .build();
        if (!roomPlayer.getRoom().getId().equals(roomId))
            return AcceptGameRoomDTO.builder()
                    .isError(true)
                    .isLast(false)
                    .message("ERROR: you're not in this room!")
                    .playerAcceptedCount(null)
                    .build();
        if (!roomPlayer.getPlayerStatus().equals(PlayerStatus.NOTACCEPTED))
            return AcceptGameRoomDTO.builder()
                    .isError(true)
                    .isLast(false)
                    .message("ERROR: you have another status")
                    .playerAcceptedCount(null)
                    .build();
        roomPlayer.setPlayerStatus(PlayerStatus.ACCEPTED);
        roomPlayerRepository.save(roomPlayer);
        int playersAcceptedCount = getAcceptedPlayersCount(roomId);
        if (playersAcceptedCount == room.getMinPlayers()){
            room.setStatus(RoomStatus.INGAME);
            roomRepository.save(room);
            return AcceptGameRoomDTO.builder()
                    .isError(false)
                    .isLast(true)
                    .message("START GAME!")
                    .playerAcceptedCount(playersAcceptedCount)
                    .build();
        }
        return AcceptGameRoomDTO.builder()
                .isError(false)
                .isLast(false)
                .message("Wait another player!")
                .playerAcceptedCount(playersAcceptedCount)
                .build();
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
