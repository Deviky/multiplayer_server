package com.cybersport.Player_Service.service;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTORequest;
import com.cybersport.Player_Service.api.v1.dto.PlayerDTOResponse;
import com.cybersport.Player_Service.api.v1.dto.PlayerFindDTO;
import com.cybersport.Player_Service.api.v1.mapper.PlayerMapper;
import com.cybersport.Player_Service.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    @Autowired
    private PlayerDataService playerDataService;
    @Autowired
    private PlayerMapper playerMapper;

    public Long createPlayer(PlayerDTORequest playerDTO){
        Player player = playerMapper.playerDTORequestToPlayer(playerDTO);
        Long id = playerDataService.savePlayer(player);
        return id;
    }

    public PlayerDTOResponse getPlayerById(Long id){
        return playerMapper.playerToPlayerDTOResponse(playerDataService.findPlayerById(id));
    }

    public List<PlayerFindDTO> getPlayersContainNickname(String nickname){
        return playerDataService.findPlayersContainNickname(nickname)
                .stream()
                .map(playerMapper::playerToPlayerFindDTO)
                .collect(Collectors.toList());
    }

    public Object renicknamePlayer(Long id, String newNickname){
        Object result = playerDataService.renickPlayer(id, newNickname);
        if (result instanceof Player)
            return playerMapper.playerToPlayerDTOResponse((Player) result);
        return result;
    }

}
