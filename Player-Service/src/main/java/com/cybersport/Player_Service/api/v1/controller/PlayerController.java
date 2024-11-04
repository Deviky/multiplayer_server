package com.cybersport.Player_Service.api.v1.controller;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTO;
import com.cybersport.Player_Service.api.v1.dto.PlayerFindDTO;
import com.cybersport.Player_Service.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<Long> createPlayer(@RequestBody PlayerDTO player){
        Long id = playerService.createPlayer(player);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerFindDTO>> searchPlayers(@RequestParam String nickname){
        List<PlayerFindDTO> players = playerService.getPlayersContainNickname(nickname);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> findPlayer(@PathVariable Long id){
        PlayerDTO player = playerService.getPlayerById(id);
        if (player == null)
                return ResponseEntity.notFound().build();
        return ResponseEntity.ok(player);
    }

    @PutMapping("/renick/{id}")
    public ResponseEntity<?> renickPlayer(@PathVariable Long id, @RequestParam String newNickname) {
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
