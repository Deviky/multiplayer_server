package com.cybersport.room.repository;

import com.cybersport.room.entity.RoomPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomPlayerRepository extends JpaRepository<RoomPlayer, Long> {
}
