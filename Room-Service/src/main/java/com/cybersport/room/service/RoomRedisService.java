package com.cybersport.room.service;

import com.cybersport.room.api.v1.dto.PlayerGameData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RoomRedisService {
    private static final String PLAYER_GAME_DATA_KEY = "room:playerGameData";
    @Autowired
    private RedisTemplate<String, PlayerGameData> redisTemplate;


    public void addPlayerGameInfo(Long roomId, PlayerGameData playerGameData) {
        String key = PLAYER_GAME_DATA_KEY + ":" + roomId;
        redisTemplate.opsForList().rightPush(key, playerGameData);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    public List<PlayerGameData> getAllPlayersGameData(Long roomId) {
        String key = PLAYER_GAME_DATA_KEY + ":" + roomId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void clearRoomData(Long roomId) {
        String key = PLAYER_GAME_DATA_KEY + ":" + roomId;
        redisTemplate.delete(key);
    }



}
