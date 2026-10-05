package com.github.edusants22.cs_player_api.jogador.models;

import com.github.edusants22.cs_player_api.jogador.enums.ArmaFavorita;
import com.github.edusants22.cs_player_api.jogador.enums.Funcao;
import com.github.edusants22.cs_player_api.jogador.enums.Pais;
import com.github.edusants22.cs_player_api.time.models.Time;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Jogador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "nomeCompleto", nullable = false, length = 100)
    private String nomeCompleto;

    @Column(name = "idade", nullable = false)
    private Integer idade;

    @Enumerated(EnumType.STRING)
    @Column(name = "pais", nullable = false)
    private Pais pais;

    @ManyToOne
    @JoinColumn (name = "time_id")
    private Time time;

    @Enumerated(EnumType.STRING)
    @Column(name = "funcao", nullable = false)
    private Funcao funcao;

    @Enumerated(EnumType.STRING)
    @Column(name = "armaFavorita", nullable = false)
    private ArmaFavorita armaFavorita;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "majors")
    private Integer majors;

    @Column(name = "inicioCarreira")
    private LocalDate inicioCarreira;

}