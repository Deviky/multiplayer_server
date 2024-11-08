package com.cybersport.Game_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private Long id;
    private List<Long> players;
    private Long creator;
    private String status;
    private Integer lowElo;
    private Integer highElo;
    private LocalDateTime createdAt;
}
