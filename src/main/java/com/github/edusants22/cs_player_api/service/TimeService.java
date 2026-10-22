package com.github.edusants22.cs_player_api.service;

import com.github.edusants22.cs_player_api.model.Equipe;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TimeService {
  private final JdbcClient db;

  public TimeService(JdbcClient db) {
    this.db = db;
  }

  public List<Equipe> listarTimes() {
    return db.sql("SELECT id,nome FROM times ORDER BY nome LIMIT 200").query(Equipe.class).list();
  }

  public Equipe buscarPorId(long id) {
    return db.sql("SELECT id,nome FROM times WHERE id=:id")
        .param("id", id)
        .query(Equipe.class)
        .optional()
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time não encontrado"));
  }

  public Optional<Equipe> buscarPorNome(String nome) {
    return db.sql("SELECT id,nome FROM times WHERE UPPER(nome)=UPPER(:nome)")
        .param("nome", normalizar(nome))
        .query(Equipe.class)
        .optional();
  }

  public static String normalizar(String nome) {
    return nome.equalsIgnoreCase("NATUS_VINCERE") ? "NAVI" : nome.trim();
  }

  public Equipe criar(String nome) {
    var key = new org.springframework.jdbc.support.GeneratedKeyHolder();
    db.sql("INSERT INTO times(nome) VALUES(:nome)").param("nome", nome.trim()).update(key, "id");
    return buscarPorId(Objects.requireNonNull(key.getKey()).longValue());
  }

  public Equipe atualizar(long id, String nome) {
    buscarPorId(id);
    db.sql("UPDATE times SET nome=:nome WHERE id=:id")
        .param("nome", nome.trim())
        .param("id", id)
        .update();
    return buscarPorId(id);
  }

  public void excluir(long id) {
    buscarPorId(id);
    db.sql("DELETE FROM times WHERE id=:id").param("id", id).update();
  }
}
