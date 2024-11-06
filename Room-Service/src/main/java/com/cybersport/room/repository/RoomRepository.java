package com.cybersport.room.repository;

import com.cybersport.room.entity.Room;
import com.cybersport.room.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT rp.room FROM RoomPlayer rp WHERE (rp.room.status = WAITING OR rp.room.status = INGAME) AND rp.playerId = :playerId")
    List<Room> findRoomsByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT r FROM Room r WHERE r.status = :status AND SIZE(r.roomPlayers) < :maxPlayers")
    List<Room> findAvailableRooms(@Param("status") RoomStatus status, @Param("maxPlayers") int maxPlayers);

}


