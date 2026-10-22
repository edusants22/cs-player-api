package com.github.edusants22.cs_player_api.controller;

import com.github.edusants22.cs_player_api.model.enums.*;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnumController {
  @GetMapping("/api/enums")
  public Map<String, Object> listar() {
    return Map.of(
        "paises", Pais.values(), "funcoes", Funcao.values(), "armas", ArmaFavorita.values());
  }
}
