package com.cybersport.Player_Service.api.v1.mapper;

import com.cybersport.Player_Service.api.v1.dto.PlayerDTORequest;
import com.cybersport.Player_Service.api.v1.dto.PlayerDTOResponse;
import com.cybersport.Player_Service.entity.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTOResponse playerToPlayerDTOResponse(Player player);

    Player playerDTORequestToPlayer(PlayerDTORequest playerDTORequest);
}
