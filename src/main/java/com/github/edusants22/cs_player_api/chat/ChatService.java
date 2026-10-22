package com.github.edusants22.cs_player_api.chat;

import com.github.edusants22.cs_player_api.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ChatService {
  public record Resposta(String resposta, List<Map<String, Object>> resultados) {}

  private final AiGateway ai;
  private final JogadorService jogadores;
  private final TimeService times;
  private final boolean enabled;
  private final int limit;

  public ChatService(
      AiGateway ai,
      JogadorService jogadores,
      TimeService times,
      @Value("${app.ai.enabled}") boolean enabled,
      @Value("${app.ai.max-records}") int limit) {
    this.ai = ai;
    this.jogadores = jogadores;
    this.times = times;
    this.enabled = enabled;
    this.limit = limit;
  }

  public Resposta responder(String mensagem) {
    if (!enabled)
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE,
          "Assistente indisponível. Configure o provedor de IA no backend.");
    var tools = new ChatTools(jogadores, times, limit);
    try {
      ai.consultar(mensagem, tools);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE,
          "Não foi possível consultar o assistente agora. Tente novamente mais tarde.");
    }
    var results = tools.resultados();
    // Only server-owned tool results are displayed, never unverified model-generated facts.
    if (results.isEmpty())
      return new Resposta(
          "Não consegui interpretar uma consulta permitida. Pergunte sobre jogadores, times, armas,"
              + " funções ou ratings. Posso apenas consultar dados.",
          List.of());
    boolean empty =
        results.stream().allMatch(r -> r.get("dados") instanceof Collection<?> c && c.isEmpty());
    return new Resposta(
        empty
            ? "Não encontrei registros para a consulta informada."
            : "Consultei os dados cadastrados. Os resultados abaixo vêm do banco; listas podem"
                + " estar limitadas.",
        results);
  }
}
