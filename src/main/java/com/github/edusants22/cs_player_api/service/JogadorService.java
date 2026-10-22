package com.github.edusants22.cs_player_api.service;

import com.github.edusants22.cs_player_api.dto.*;
import com.github.edusants22.cs_player_api.model.Jogador;
import com.github.edusants22.cs_player_api.repository.JogadorRepository;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JogadorService {
  private final JogadorRepository repository;
  private final TimeService times;

  public JogadorService(JogadorRepository repository, TimeService times) {
    this.repository = repository;
    this.times = times;
  }

  public List<Jogador> listarTodos() {
    return repository.listarTodos();
  }

  public Jogador buscarPorId(long id) {
    return repository
        .buscarPorId(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
  }

  public List<Jogador> consultar(
      String time,
      String nick,
      String pais,
      String funcao,
      String arma,
      Double rating,
      int limite) {
    return repository.consultar(
        time, nick, pais, funcao, arma, rating, Math.max(1, Math.min(limite, 200)));
  }

  public Jogador criarJogador(CriarJogadorDTO d) {
    times.buscarPorId(d.timeId());
    return buscarPorId(repository.criar(d));
  }

  public Jogador atualizar(long id, CriarJogadorDTO d) {
    buscarPorId(id);
    times.buscarPorId(d.timeId());
    repository.atualizar(id, d);
    return buscarPorId(id);
  }

  public void excluir(long id) {
    if (repository.excluir(id) == 0)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado");
  }

  public Map<String, Long> contarPorPais() {
    return repository.contarPorPais();
  }

  public long contarPorTime(String time) {
    return repository.contarPorTime(time);
  }

  public OptionalDouble mediaPorTime(String time) {
    return repository.mediaPorTime(time);
  }
}
