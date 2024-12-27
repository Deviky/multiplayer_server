package com.cybersport.room.api.v1.controller;

import com.cybersport.room.api.v1.dto.*;
import com.cybersport.room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<CreateRoomResponseDTO> createRoom(
            @RequestHeader("X-Player-Id") Long playerId,
            @RequestBody CreateRoomRequestDTO request) {

        int minPlayers = request.getMinPlayers();
        if (minPlayers < 2 || minPlayers > 6 || minPlayers % 2 != 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(CreateRoomResponseDTO.builder()
                    .roomId(null)
                    .message("Bad players count!")
                    .build());
        }

        CreateRoomResponseDTO response = roomService.createRoom(playerId, minPlayers);
        if (response.getRoomId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/getAllRoomsNow")
    public ResponseEntity<List<RoomDTO>> getAllRoomNow(@RequestHeader("X-Player-Id") Long playerId){
        System.out.println(playerId);
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @PostMapping("/joinToRoom/{roomId}")
    public ResponseEntity<RoomInfoDTO> joinToRoom(
            @RequestHeader("X-Player-Id") Long playerId,
            @PathVariable Long roomId) {

        JoinRoomResponseDTO response = roomService.joinToRoom(playerId, roomId);
        if (response.isError()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", response.getMessage())
                    .body(null);
        } else {
            return ResponseEntity.ok(response.getRoomInfo());
        }
    }

    @DeleteMapping("/leaveFromRoom/{roomId}")
    public ResponseEntity<String> leaveFromRoom(
            @RequestHeader("X-Player-Id") Long playerId,
            @PathVariable Long roomId) {

        LeaveRoomResponseDTO response = roomService.leaveFromRoom(playerId, roomId);
        if (response.isError()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response.getMessage());
        } else {
            return ResponseEntity.ok(response.getMessage());
        }
    }

    @PostMapping("/startGame/{roomId}")
    public ResponseEntity<String> startGame(
            @RequestHeader("X-Player-Id") Long playerId,
            @PathVariable Long roomId) {

        StartGameRoomResponse response = roomService.startGame(playerId, roomId);
        if (response.isError()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response.getMessage());
        } else {
            return ResponseEntity.ok(response.getMessage());
        }
    }

    @PostMapping("/acceptGame/{roomId}")
    public ResponseEntity<String> acceptGame(
            @RequestHeader("X-Player-Id") Long playerId,
            @PathVariable Long roomId,
            @RequestBody PlayerGameData playerGameData) {

        playerGameData.setPlayerId(playerId); // Передаем playerId в данные игрока
        playerGameData.setTeam(null);

        AcceptGameRoomDTO response = roomService.acceptGame(playerGameData, roomId);
        if (response.isError()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response.getMessage());
        } else {
            return ResponseEntity.ok(response.getMessage());
        }
    }
}
