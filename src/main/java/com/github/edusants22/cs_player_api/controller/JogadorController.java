package com.github.edusants22.cs_player_api.controller;

import com.github.edusants22.cs_player_api.dto.*;
import com.github.edusants22.cs_player_api.model.*;
import com.github.edusants22.cs_player_api.service.*;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jogadores")
public class JogadorController {
  private final JogadorService service;
  private final TimeService times;

  public JogadorController(JogadorService service, TimeService times) {
    this.service = service;
    this.times = times;
  }

  @GetMapping
  public List<Jogador> listar(
      @RequestParam(defaultValue = "") String pais,
      @RequestParam(required = false) Long timeId,
      @RequestParam(defaultValue = "") String funcao) {
    return service.consultar(
        timeId == null ? "" : times.buscarPorId(timeId).nome(), "", pais, funcao, "", null, 200);
  }

  @GetMapping("/{id}")
  public Jogador buscar(@PathVariable long id) {
    return service.buscarPorId(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Jogador criar(@Valid @RequestBody CriarJogadorDTO d) {
    return service.criarJogador(d);
  }

  @PutMapping("/{id}")
  public Jogador atualizar(@PathVariable long id, @Valid @RequestBody CriarJogadorDTO d) {
    return service.atualizar(id, d);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable long id) {
    service.excluir(id);
  }
}
