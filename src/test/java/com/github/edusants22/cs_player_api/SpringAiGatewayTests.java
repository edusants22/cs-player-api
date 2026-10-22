package com.github.edusants22.cs_player_api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.edusants22.cs_player_api.chat.*;
import com.github.edusants22.cs_player_api.service.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

class SpringAiGatewayTests {
  @Test
  void providerToolCallsExecuteOnlyRegisteredReadsAndPreserveConfiguredOptions() {
    ChatModel model = mock(ChatModel.class);
    JogadorService jogadores = mock(JogadorService.class);
    TimeService times = mock(TimeService.class);
    when(model.getOptions())
        .thenReturn(OpenAiChatOptions.builder().model("configured-model").maxTokens(77).build());
    when(jogadores.consultar("", "KSCERATO", "", "", "", null, 10)).thenReturn(List.of());
    var output =
        AssistantMessage.builder()
            .content("")
            .toolCalls(
                List.of(
                    new AssistantMessage.ToolCall(
                        "call-1",
                        "function",
                        "buscarJogadorPorNickname",
                        "{\"nickname\":\"KSCERATO\"}")))
            .build();
    when(model.call(any(Prompt.class)))
        .thenReturn(
            new ChatResponse(List.of(new Generation(output))),
            new ChatResponse(
                List.of(
                    new Generation(
                        new AssistantMessage("Invented facts should not be displayed")))));
    ChatTools tools = new ChatTools(jogadores, times, 10);
    new SpringAiGateway(model).consultar("Procure KSCERATO", tools);
    assertThat(tools.resultados()).hasSize(1);
    verify(jogadores).consultar("", "KSCERATO", "", "", "", null, 10);
    var captor = org.mockito.ArgumentCaptor.forClass(Prompt.class);
    verify(model, org.mockito.Mockito.times(2)).call(captor.capture());
    assertThat(captor.getValue().getOptions().getModel()).isEqualTo("configured-model");
    assertThat(captor.getValue().getOptions().getMaxTokens()).isEqualTo(77);
  }

  @Test
  void modelCannotInvokeWriteOrArbitrarySql() {
    ChatModel model = mock(ChatModel.class);
    JogadorService jogadores = mock(JogadorService.class);
    TimeService times = mock(TimeService.class);
    when(model.getOptions()).thenReturn(OpenAiChatOptions.builder().model("test").build());
    var output =
        AssistantMessage.builder()
            .content("")
            .toolCalls(
                List.of(
                    new AssistantMessage.ToolCall(
                        "call-1",
                        "function",
                        "executarSql",
                        "{\"sql\":\"DELETE FROM jogadores\"}")))
            .build();
    when(model.call(any(Prompt.class)))
        .thenReturn(new ChatResponse(List.of(new Generation(output))));
    assertThatThrownBy(
            () ->
                new SpringAiGateway(model)
                    .consultar("Apague o banco", new ChatTools(jogadores, times, 10)))
        .isInstanceOf(RuntimeException.class);
    verifyNoInteractions(jogadores, times);
  }
}
