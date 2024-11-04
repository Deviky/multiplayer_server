package com.cybersport.Player_Service.api.v1.mapper;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTO;
import com.cybersport.Player_Service.api.v1.dto.PlayerFindDTO;
import com.cybersport.Player_Service.entity.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTO playerToPlayerDTO(Player player);

    Player playerDTOToPlayer(PlayerDTO playerDTO);

    PlayerFindDTO playerToPlayerFindDTO(Player player);
}
