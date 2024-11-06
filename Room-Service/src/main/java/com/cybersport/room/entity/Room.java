package com.cybersport.room.entity;

import com.cybersport.room.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomPlayer> roomPlayers;
    private Long creator;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    private Integer lowElo;
    private Integer highElo;
    private Integer minPlayers;
    private LocalDateTime createdAt;
}
