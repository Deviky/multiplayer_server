package com.cybersport.Player_Service.service;

import com.cybersport.Player_Service.entity.Player;
import com.cybersport.Player_Service.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerDataService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    public Long savePlayer(Player player){
        player.setElo(1000);
        playerRepository.save(player);
        return player.getId();
    }

    public List<Player> findPlayersContainNickname(String nickname) {
        List<Player> players = playerRepository.findByNicknameContainingIgnoreCase(nickname);
        return players;
    }

    @Cacheable("player")
    public Player findPlayerById(Long id){
        return playerRepository.findById(id).orElse(null);
    }

    @CachePut("player")
    public Object renickPlayer(Long id, String newNickname) {
        Player player = findPlayerById(id);
        if (player == null){
            return null;
        }
        Pair<Integer, Integer> timeToUpdateNickname = redisCacheService.getLastNicknameChange(id);
        if (timeToUpdateNickname.getFirst() != 0 || timeToUpdateNickname.getSecond() != 0)
            return timeToUpdateNickname;

        player.setNickname(newNickname);
        playerRepository.save(player);
        redisCacheService.updateLastNicknameChange(id);
        return player;
    }

}
