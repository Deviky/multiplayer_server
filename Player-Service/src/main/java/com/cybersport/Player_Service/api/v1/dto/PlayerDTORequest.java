package com.cybersport.Player_Service.api.v1.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTORequest {
    private String nickname;
}

