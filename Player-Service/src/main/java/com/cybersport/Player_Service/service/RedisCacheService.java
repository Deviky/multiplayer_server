package com.cybersport.Player_Service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RedisCacheService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Pair<Integer, Integer> getLastNicknameChange(Long playerId) {
        String lastChange = redisTemplate.opsForValue().get("playerLastNicknameChange::" + playerId);
        if (lastChange == null)
            return Pair.of(0,0);
        else{
            LocalDateTime lastChangeTime = LocalDateTime.parse(lastChange, FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            Duration timeElapsed = Duration.between(lastChangeTime, now);

            int hoursElapsed = (int) timeElapsed.toHours();
            if (hoursElapsed >= 24) {
                return Pair.of(0,0);
            }
            // Осталось до 24 часов с последнего изменения
            int hoursRemaining = 23 - hoursElapsed;
            int minutesRemaining = 59 - timeElapsed.toMinutesPart();

            return Pair.of(hoursRemaining, minutesRemaining);
        }
    }

    public void updateLastNicknameChange(Long playerId) {
        redisTemplate.opsForValue().set(
                "playerLastNicknameChange::" + playerId,
                LocalDateTime.now().format(FORMATTER),
                Duration.ofDays(1) // Устанавливаем время жизни ключа 24 часа
        );
    }
}
