package com.github.edusants22.cs_player_api.chat;

import com.github.edusants22.cs_player_api.model.*;
import com.github.edusants22.cs_player_api.service.*;
import java.util.*;
import org.springframework.ai.tool.annotation.Tool;

/** Request-scoped tool collection. Only public game data is returned to the model. */
public class ChatTools {
  private final JogadorService jogadores;
  private final TimeService times;
  private final int limit;
  private int calls;
  private final List<Map<String, Object>> resultados = new ArrayList<>();

  public ChatTools(JogadorService jogadores, TimeService times, int limit) {
    this.jogadores = jogadores;
    this.times = times;
    this.limit = Math.max(1, Math.min(limit, 100));
  }

  public List<Map<String, Object>> resultados() {
    return List.copyOf(resultados);
  }

  private Map<String, Object> result(String consulta, Object dados) {
    if (++calls > 8) throw new IllegalArgumentException("Limite de ferramentas atingido");
    var r = Map.<String, Object>of("consulta", consulta, "dados", dados, "limiteRegistros", limit);
    resultados.add(r);
    return r;
  }

  private List<Jogador> query(
      String time, String nick, String pais, String funcao, String arma, Double rating, int n) {
    return jogadores.consultar(
        time, nick, pais, funcao, arma, rating, Math.min(limit, Math.max(1, n)));
  }

  private String team(String name) {
    return times.buscarPorNome(name).map(Equipe::nome).orElse(null);
  }

  @Tool(
      description =
          "Lista os times cadastrados. Resultado limitado; não representa o total se atingir o"
              + " limite.")
  public Object listarTimes() {
    return result("Times cadastrados", times.listarTimes().stream().limit(limit).toList());
  }

  @Tool(description = "Busca time por nome; NAVI equivale a NATUS_VINCERE.")
  public Object buscarTimePorNome(String nome) {
    return result(
        "Time: " + nome,
        times.buscarPorNome(nome).map(t -> (Object) t).orElse("Time não encontrado"));
  }

  @Tool(description = "Lista jogadores de um time cadastrado.")
  public Object buscarJogadoresPorTime(String nomeTime) {
    String t = team(nomeTime);
    return result(
        "Jogadores do time " + nomeTime,
        t == null ? "Time não encontrado" : query(t, "", "", "", "", null, limit));
  }

  @Tool(description = "Busca jogador pelo nickname exato, sem diferenciar maiúsculas.")
  public Object buscarJogadorPorNickname(String nickname) {
    return result("Jogador: " + nickname, query("", nickname, "", "", "", null, limit));
  }

  @Tool(
      description =
          "Lista jogadores por país. Use BRASIL, UCRANIA, RUSSIA, FRANCA, BOSNIA, DINAMARCA,"
              + " LETONIA, CANADA, MONGOLIA, ESTONIA, ISRAEL, FINLANDIA ou HUNGRIA.")
  public Object buscarJogadoresPorPais(String pais) {
    return result(
        "País: " + pais, query("", "", pais.toUpperCase(Locale.ROOT), "", "", null, limit));
  }

  @Tool(description = "Lista por função: RIFLER, AWPER, IGL, SUPPORT, LURKER ou ENTRY.")
  public Object buscarJogadoresPorFuncao(String funcao) {
    return result(
        "Função: " + funcao, query("", "", "", funcao.toUpperCase(Locale.ROOT), "", null, limit));
  }

  @Tool(description = "Lista por arma: AK47, M4A1S, M4A4, AWP, DEAGLE, GLOCK ou USP.")
  public Object buscarJogadoresPorArma(String arma) {
    return result(
        "Arma: " + arma, query("", "", "", "", arma.toUpperCase(Locale.ROOT), null, limit));
  }

  @Tool(description = "Lista jogadores com rating maior ou igual ao mínimo.")
  public Object buscarJogadoresPorRatingMinimo(double rating) {
    if (!Double.isFinite(rating) || rating < 0 || rating > 5)
      throw new IllegalArgumentException("Rating inválido");
    return result("Rating mínimo: " + rating, query("", "", "", "", "", rating, limit));
  }

  @Tool(description = "Ranking decrescente de rating. Limite de 1 até o máximo permitido.")
  public Object buscarTopJogadores(int limite) {
    return result("Ranking por rating", query("", "", "", "", "", null, limite));
  }

  @Tool(
      description =
          "Média exata do rating de todos os jogadores de um time; não usa amostra limitada.")
  public Object calcularRatingMedioPorTime(String nomeTime) {
    String t = team(nomeTime);
    return result(
        "Rating médio de " + nomeTime,
        t == null
            ? "Time não encontrado"
            : jogadores.mediaPorTime(t).stream()
                .boxed()
                .findFirst()
                .map(v -> (Object) v)
                .orElse("Nenhum jogador cadastrado"));
  }

  @Tool(description = "Quantidade exata de jogadores de um time.")
  public Object contarJogadoresPorTime(String nomeTime) {
    String t = team(nomeTime);
    return result(
        "Quantidade de jogadores de " + nomeTime,
        t == null ? "Time não encontrado" : jogadores.contarPorTime(t));
  }

  @Tool(description = "Conta todos os jogadores por país, usando os dados cadastrados.")
  public Object contarJogadoresPorPais() {
    return result("Quantidade por país", jogadores.contarPorPais());
  }

  @Tool(
      description =
          "Compara quantidades e rating médio de todos os times cadastrados; permite identificar o"
              + " time com mais jogadores.")
  public Object analisarTimes() {
    return result(
        "Estatísticas dos times",
        times.listarTimes().stream()
            .limit(limit)
            .map(
                t ->
                    Map.of(
                        "time",
                        t.nome(),
                        "jogadores",
                        jogadores.contarPorTime(t.nome()),
                        "ratingMedio",
                        jogadores.mediaPorTime(t.nome()).stream()
                            .boxed()
                            .findFirst()
                            .map(v -> (Object) v)
                            .orElse("Sem jogadores")))
            .toList());
  }
}
