package com.cybersport.Player_Service.repository;

import com.cybersport.Player_Service.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByNicknameContainingIgnoreCase(String nickname);
}
