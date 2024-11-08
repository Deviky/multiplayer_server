package com.cybersport.room.api.v1.dto;


import com.cybersport.room.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomInfoDTO {
    private Long id;
    private List<Player> players;
    private Long creator;
    private RoomStatus status;
    private Integer lowElo;
    private Integer highElo;
    private LocalDateTime createdAt;
}
