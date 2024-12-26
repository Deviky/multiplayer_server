package com.cybersport.Player_Service.api.v1.controller;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTOResponse;
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


    @GetMapping("/search")
    public ResponseEntity<List<PlayerDTOResponse>> searchPlayers(@RequestParam String nickname){
        List<PlayerDTOResponse> players = playerService.getPlayersContainNickname(nickname);
        return ResponseEntity.ok(players);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTOResponse> findPlayer(@PathVariable Long id){
        PlayerDTOResponse player = playerService.getPlayerById(id);
        if (player == null)
                return ResponseEntity.notFound().build();
        return ResponseEntity.ok(player);
    }
}
