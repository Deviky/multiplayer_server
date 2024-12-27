package com.cybersport.room.api.v1.mapper;


import com.cybersport.room.api.v1.dto.RoomDTO;
import com.cybersport.room.entity.Room;
import com.cybersport.room.entity.RoomPlayer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomMapper {

    // Метод для преобразования Room в RoomDTO
    public RoomDTO roomToRoomDTO(Room room) {
        if (room == null) {
            return null;
        }

        List<Long> players = mapPlayers(room.getRoomPlayers());

        return RoomDTO.builder()
                .id(room.getId())
                .players(players)
                .creator(room.getCreator())
                .status(room.getStatus())
                .lowElo(room.getLowElo())
                .highElo(room.getHighElo())
                .createdAt(room.getCreatedAt())
                .build();
    }

    // Метод для маппинга игроков
    private List<Long> mapPlayers(List<RoomPlayer> roomPlayers) {
        if (roomPlayers == null || roomPlayers.isEmpty()) {
            return Collections.emptyList();
        }
        return roomPlayers.stream()
                .map(RoomPlayer::getPlayerId)
                .collect(Collectors.toList());
    }
}