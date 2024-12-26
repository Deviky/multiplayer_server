package com.cybersport.Player_Service.api.v1.controller;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTORequest;
import com.cybersport.Player_Service.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("playerPrivate")
public class PrivatePlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<String> createPlayer(@RequestBody PlayerDTORequest player){
        playerService.createPlayer(player);
        return new ResponseEntity<>("Игрок создан!", HttpStatus.CREATED);
    }

    @PutMapping("/renick")
    public ResponseEntity<?> renickPlayer(@RequestBody Long id, @RequestBody String newNickname) {
        Object result = playerService.renicknamePlayer(id, newNickname);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        if (result instanceof Pair) {
            Pair<Integer, Integer> timeRemaining = (Pair<Integer, Integer>) result;
            long hours = timeRemaining.getFirst();
            long minutes = timeRemaining.getSecond();
            String message = "Nickname change available in " + hours + " hours and " + minutes + " minutes";
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }
        return ResponseEntity.ok(result);
    }
}
