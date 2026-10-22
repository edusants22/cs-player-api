package com.github.edusants22.cs_player_api.controller;

import com.github.edusants22.cs_player_api.dto.CriarTimeDTO;
import com.github.edusants22.cs_player_api.model.Equipe;
import com.github.edusants22.cs_player_api.service.TimeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/times")
public class TimeController {
  private final TimeService service;

  public TimeController(TimeService service) {
    this.service = service;
  }

  @GetMapping
  public List<Equipe> listar() {
    return service.listarTimes();
  }

  @GetMapping("/{id}")
  public Equipe buscar(@PathVariable long id) {
    return service.buscarPorId(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Equipe criar(@Valid @RequestBody CriarTimeDTO d) {
    return service.criar(d.nome());
  }

  @PutMapping("/{id}")
  public Equipe atualizar(@PathVariable long id, @Valid @RequestBody CriarTimeDTO d) {
    return service.atualizar(id, d.nome());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable long id) {
    service.excluir(id);
  }
}
