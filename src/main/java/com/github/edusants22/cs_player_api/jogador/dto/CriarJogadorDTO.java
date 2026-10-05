package com.github.edusants22.cs_player_api.jogador.dto;

import com.github.edusants22.cs_player_api.jogador.enums.ArmaFavorita;
import com.github.edusants22.cs_player_api.jogador.enums.Funcao;
import com.github.edusants22.cs_player_api.jogador.enums.Pais;
import com.github.edusants22.cs_player_api.time.models.Time;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarJogadorDTO {

    private String nickname;
    private String nomeCompleto;
    private Integer idade;
    private Pais pais;
    private Long timeId;
    private Funcao funcao;
    private ArmaFavorita armaFavorita;
    private Double rating;

}
