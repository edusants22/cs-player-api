package com.github.edusants22.cs_player_api.repository;

import com.github.edusants22.cs_player_api.dto.CriarJogadorDTO;
import com.github.edusants22.cs_player_api.model.*;
import com.github.edusants22.cs_player_api.model.enums.*;
import java.util.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JogadorRepository {
  private final JdbcClient db;

  public JogadorRepository(JdbcClient db) {
    this.db = db;
  }

  private static final String SELECT =
      "SELECT j.*, t.nome AS time_nome FROM jogadores j JOIN times t ON t.id=j.time_id";
  private static final RowMapper<Jogador> MAP =
      (r, n) -> {
        Jogador j = new Jogador();
        j.setId(r.getLong("id"));
        j.setNickname(r.getString("nickname"));
        j.setNomeCompleto(r.getString("nome_completo"));
        j.setIdade(r.getInt("idade"));
        j.setPais(Pais.valueOf(r.getString("pais")));
        j.setTime(new Equipe(r.getLong("time_id"), r.getString("time_nome")));
        j.setFuncao(Funcao.valueOf(r.getString("funcao")));
        j.setArmaFavorita(ArmaFavorita.valueOf(r.getString("arma_favorita")));
        j.setRating(r.getDouble("rating"));
        j.setMajors(r.getInt("majors"));
        j.setInicioCarreira(r.getObject("inicio_carreira", java.time.LocalDate.class));
        return j;
      };

  public List<Jogador> listarTodos() {
    return db.sql(SELECT + " ORDER BY j.id").query(MAP).list();
  }

  public Optional<Jogador> buscarPorId(long id) {
    return db.sql(SELECT + " WHERE j.id=:id").param("id", id).query(MAP).optional();
  }

  public List<Jogador> consultar(
      String time,
      String nickname,
      String pais,
      String funcao,
      String arma,
      Double rating,
      int limite) {
    return db.sql(
            SELECT
                + " WHERE (:time='' OR UPPER(t.nome)=UPPER(:time)) AND (:nick='' OR"
                + " UPPER(j.nickname)=UPPER(:nick)) AND (:pais='' OR j.pais=:pais) AND (:funcao=''"
                + " OR j.funcao=:funcao) AND (:arma='' OR j.arma_favorita=:arma) AND"
                + " j.rating>=:rating ORDER BY j.rating DESC,j.id LIMIT :limite")
        .param("time", time)
        .param("nick", nickname)
        .param("pais", pais)
        .param("funcao", funcao)
        .param("arma", arma)
        .param("rating", rating == null ? 0 : rating)
        .param("limite", limite)
        .query(MAP)
        .list();
  }

  public long criar(CriarJogadorDTO d) {
    var key = new org.springframework.jdbc.support.GeneratedKeyHolder();
    params(
            db.sql(
                "INSERT INTO"
                    + " jogadores(nickname,nome_completo,idade,pais,time_id,funcao,arma_favorita,rating)"
                    + " VALUES(:nickname,:nome,:idade,:pais,:time,:funcao,:arma,:rating)"),
            d)
        .update(key, "id");
    return Objects.requireNonNull(key.getKey()).longValue();
  }

  public int atualizar(long id, CriarJogadorDTO d) {
    return params(
            db.sql(
                "UPDATE jogadores SET"
                    + " nickname=:nickname,nome_completo=:nome,idade=:idade,pais=:pais,time_id=:time,funcao=:funcao,arma_favorita=:arma,rating=:rating"
                    + " WHERE id=:id"),
            d)
        .param("id", id)
        .update();
  }

  private JdbcClient.StatementSpec params(JdbcClient.StatementSpec s, CriarJogadorDTO d) {
    return s.param("nickname", d.nickname().trim())
        .param("nome", d.nomeCompleto().trim())
        .param("idade", d.idade())
        .param("pais", d.pais().name())
        .param("time", d.timeId())
        .param("funcao", d.funcao().name())
        .param("arma", d.armaFavorita().name())
        .param("rating", d.rating());
  }

  public long contarPorTime(String time) {
    return db.sql(
            "SELECT COUNT(*) FROM jogadores j JOIN times t ON t.id=j.time_id WHERE"
                + " UPPER(t.nome)=UPPER(:time)")
        .param("time", time)
        .query(Long.class)
        .single();
  }

  public OptionalDouble mediaPorTime(String time) {
    return db.sql(
            "SELECT AVG(j.rating) FROM jogadores j JOIN times t ON t.id=j.time_id WHERE"
                + " UPPER(t.nome)=UPPER(:time)")
        .param("time", time)
        .query(
            (r, n) ->
                r.getObject(1) == null ? OptionalDouble.empty() : OptionalDouble.of(r.getDouble(1)))
        .single();
  }

  public Map<String, Long> contarPorPais() {
    var counts = new TreeMap<String, Long>();
    db.sql("SELECT pais,COUNT(*) AS quantidade FROM jogadores GROUP BY pais")
        .query((r, n) -> Map.entry(r.getString("pais"), r.getLong("quantidade")))
        .list()
        .forEach(e -> counts.put(e.getKey(), e.getValue()));
    return counts;
  }

  public int excluir(long id) {
    return db.sql("DELETE FROM jogadores WHERE id=:id").param("id", id).update();
  }
}
