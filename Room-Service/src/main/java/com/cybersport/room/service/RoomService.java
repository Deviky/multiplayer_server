package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.RoomDTO;
import com.cybersport.room.api.v1.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RoomService {

    @Autowired
    private RoomServiceData roomServiceData;

    @Autowired
    private RoomMapper roomMapper;

    public String createRoom(Long creatorId){
        return roomServiceData.createRoom(creatorId);
    }

    public List<RoomDTO> getAvailableRooms(){
        return roomServiceData.getAvailableRooms()
                .stream()
                .map(roomMapper::roomToRoomDTO)
                .collect(Collectors.toList());
    }

    public String joinToRoom(Long playerId, Long roomId){
        return roomServiceData.joinToRoom(playerId, roomId);
    }

    public String leaveFromRoom(Long playerId, Long roomId){
        return roomServiceData.leaveFromRoom(playerId, roomId);
    }

}