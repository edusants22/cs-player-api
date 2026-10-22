package com.github.edusants22.cs_player_api.dto;

import com.github.edusants22.cs_player_api.model.enums.*;
import jakarta.validation.constraints.*;

public record CriarJogadorDTO(
    @NotBlank @Size(max = 80) String nickname,
    @NotBlank @Size(max = 150) String nomeCompleto,
    @NotNull @Min(13) @Max(100) Integer idade,
    @NotNull Pais pais,
    @NotNull @Positive Long timeId,
    @NotNull Funcao funcao,
    @NotNull ArmaFavorita armaFavorita,
    @NotNull @DecimalMin("0") @DecimalMax("5") Double rating) {}
