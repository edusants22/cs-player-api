package com.github.edusants22.cs_player_api.dto;

import jakarta.validation.constraints.*;

public record CriarTimeDTO(@NotBlank @Size(max = 100) String nome) {}
