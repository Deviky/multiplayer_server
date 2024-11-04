package com.cybersport.room.api.v1.dto;

import com.cybersport.room.entity.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long id;
    private List<Long> players;
    private Long creator;
    private RoomStatus status;
    private Integer lowElo;
    private Integer highElo;
    private LocalDateTime createdAt;
}
