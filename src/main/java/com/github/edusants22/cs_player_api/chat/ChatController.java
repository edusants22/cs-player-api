package com.github.edusants22.cs_player_api.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
  public record Mensagem(@NotBlank @Size(max = 2000) String mensagem) {}

  private final ChatService service;
  private final ChatRateLimiter limiter;

  public ChatController(ChatService service, ChatRateLimiter limiter) {
    this.service = service;
    this.limiter = limiter;
  }

  @PostMapping("/mensagem")
  @PreAuthorize("hasAnyRole('ADMIN','USER')")
  public ChatService.Resposta responder(
      @Valid @RequestBody Mensagem body, @AuthenticationPrincipal Jwt jwt) {
    limiter.check(jwt.getSubject());
    return service.responder(body.mensagem());
  }
}
