package com.cybersport.Player_Service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {
    @Id
    Long id;
    String nickname;
    Integer elo;
}
