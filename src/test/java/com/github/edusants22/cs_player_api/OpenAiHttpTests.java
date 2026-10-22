package com.github.edusants22.cs_player_api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.edusants22.cs_player_api.chat.*;
import com.github.edusants22.cs_player_api.model.Equipe;
import com.github.edusants22.cs_player_api.service.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.*;

class OpenAiHttpTests {
  @Test
  void officialSdkCompletesToolRoundTripAgainstMockHttpProvider() throws Exception {
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    var calls = new AtomicInteger();
    var requests = new ArrayList<String>();
    server.createContext(
        "/v1/chat/completions",
        e -> {
          requests.add(new String(e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
          String message =
              calls.getAndIncrement() == 0
                  ? "{\"role\":\"assistant\",\"content\":null,\"tool_calls\":[{\"id\":\"call-1\",\"type\":\"function\",\"function\":{\"name\":\"listarTimes\",\"arguments\":\"{}\"}}]}"
                  : "{\"role\":\"assistant\",\"content\":\"Invented team that must never be"
                      + " displayed\"}";
          byte[] body =
              ("{\"id\":\"chat-test\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"configured-http\",\"choices\":[{\"index\":0,\"message\":"
                      + message
                      + ",\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":1,\"completion_tokens\":1,\"total_tokens\":2}}")
                  .getBytes(StandardCharsets.UTF_8);
          e.getResponseHeaders().add("Content-Type", "application/json");
          e.sendResponseHeaders(200, body.length);
          e.getResponseBody().write(body);
          e.close();
        });
    server.start();
    try {
      var model =
          OpenAiChatModel.builder()
              .options(
                  OpenAiChatOptions.builder()
                      .apiKey("test-no-external-calls")
                      .baseUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/v1")
                      .model("configured-http")
                      .maxTokens(123)
                      .maxRetries(0)
                      .timeout(Duration.ofSeconds(3))
                      .build())
              .build();
      var jogadores = mock(JogadorService.class);
      var times = mock(TimeService.class);
      when(times.listarTimes()).thenReturn(List.of(new Equipe(1L, "FURIA")));
      var service = new ChatService(new SpringAiGateway(model), jogadores, times, true, 10);
      var answer = service.responder("Quais times estão cadastrados?");
      assertThat(answer.resultados()).hasSize(1);
      assertThat(answer.resultados().getFirst().get("dados"))
          .isEqualTo(List.of(new Equipe(1L, "FURIA")));
      assertThat(answer.resposta()).doesNotContain("Invented");
      assertThat(calls.get()).isEqualTo(2);
      assertThat(requests.getLast()).contains("configured-http", "FURIA", "call-1");
    } finally {
      server.stop(0);
    }
  }
}
