package org.example.datasource.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_field", schema = "tictactoe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameFieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field", nullable = false)
    private String field;
}