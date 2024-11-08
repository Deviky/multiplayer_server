package com.cybersport.Game_Service.component;

import com.cybersport.Game_Service.dto.Room;
import com.cybersport.Game_Service.server.GameServer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameListener {

    @Autowired
    private GameServer gameServer;

    @RabbitListener(queues = {"game_queue"})
    public void recieveMessageFromRoomService(Room room){

    }
}
