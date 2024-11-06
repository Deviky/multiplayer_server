package com.cybersport.room.repository;

import com.cybersport.room.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
    List<RoomPlayer> findByRoomId(Long roomId);
    RoomPlayer findByPlayerId(Long playerId);
}
