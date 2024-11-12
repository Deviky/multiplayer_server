package com.cybersport.room.service;


import com.cybersport.room.api.v1.dto.RoomGameData;
import org.springframework.amqp.core.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final Queue gameQueue;

    public void notifyGameService(RoomGameData roomGameData) {
        rabbitTemplate.convertAndSend(gameQueue.getName(), roomGameData);
    }

}
