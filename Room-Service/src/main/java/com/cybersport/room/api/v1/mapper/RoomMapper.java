package com.cybersport.room.api.v1.mapper;


import com.cybersport.room.api.v1.dto.RoomDTO;
import com.cybersport.room.entity.Room;
import com.cybersport.room.entity.RoomPlayer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "players", expression = "java(mapPlayers(room.getRoomPlayers()))")
    RoomDTO roomToRoomDTO(Room room);

    default List<Long> mapPlayers(List<RoomPlayer> roomPlayers) {
        if (roomPlayers == null || roomPlayers.isEmpty()) {
            return Collections.emptyList();
        }
        return roomPlayers.stream()
                .map(RoomPlayer::getPlayerId) // Убедитесь, что метод getPlayerId() существует
                .collect(Collectors.toList());
    }
}