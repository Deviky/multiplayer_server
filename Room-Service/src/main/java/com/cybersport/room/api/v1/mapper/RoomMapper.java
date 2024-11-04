package com.cybersport.room.api.v1.mapper;


import com.cybersport.room.api.v1.dto.RoomDTO;
import com.cybersport.room.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomDTO roomToRoomDTO(Room room);
}
