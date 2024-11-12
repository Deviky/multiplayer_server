package com.cybersport.Game_Service.controller;

import com.cybersport.Game_Service.dto.RoomGameData;
import com.cybersport.Game_Service.server.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class Controller {

    @Autowired
    private GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<Integer> startGame(@RequestBody RoomGameData room){
        return ResponseEntity.ok(gameService.createServer(room));
    }

}
