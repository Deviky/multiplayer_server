package com.cybersport.room.entity;

import com.cybersport.room.enums.PlayerStatus;
import com.cybersport.room.enums.PlayerTeam;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long playerId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;
    @Enumerated(EnumType.STRING)
    private PlayerTeam playerTeam;

}
