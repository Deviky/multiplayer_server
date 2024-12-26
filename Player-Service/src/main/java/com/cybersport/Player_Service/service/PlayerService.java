package com.cybersport.Player_Service.service;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTORequest;
import com.cybersport.Player_Service.api.v1.dto.PlayerDTOResponse;
import com.cybersport.Player_Service.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {
    @Autowired
    private PlayerDataService playerDataService;


    public void createPlayer(PlayerDTORequest playerDTO){
        Player player = Player.builder()
                .id(playerDTO.getId())
                .nickname(playerDTO.getNickname())
                .elo(1200)
                .build();
        playerDataService.savePlayer(player);
    }

    public PlayerDTOResponse getPlayerById(Long id){
        Player player = playerDataService.findPlayerById(id);
        return PlayerDTOResponse.builder()
                .id(player.getId())
                .nickname(player.getNickname())
                .elo(player.getElo())
                .build();
    }

    public List<PlayerDTOResponse> getPlayersContainNickname(String nickname) {
        // Находим список сущностей и преобразуем каждую в PlayerDTOResponse
        return playerDataService.findPlayersContainNickname(nickname)
                .stream()
                .map(player -> PlayerDTOResponse.builder()
                        .id(player.getId())
                        .nickname(player.getNickname())
                        .elo(player.getElo())
                        .build())
                .collect(Collectors.toList());
    }

    public Object renicknamePlayer(Long id, String newNickname) {
        // Выполняем обновление ника
        Object result = playerDataService.renickPlayer(id, newNickname);

        // Если результат - сущность Player, преобразуем её в PlayerDTOResponse
        if (result instanceof Player) {
            Player player = (Player) result;
            return PlayerDTOResponse.builder()
                    .id(player.getId())
                    .nickname(player.getNickname())
                    .elo(player.getElo())
                    .build();
        }

        // Иначе возвращаем результат как есть
        return result;
    }


}
