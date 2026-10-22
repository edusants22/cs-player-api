package com.github.edusants22.cs_player_api.chat;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ChatRateLimiter {
  private record Window(long minute, int count) {}

  private final Map<String, Window> windows = new HashMap<>();
  private final int max;

  public ChatRateLimiter(@Value("${app.ai.requests-per-minute}") int max) {
    this.max = Math.max(1, max);
  }

  public synchronized void check(String subject) {
    long minute = Instant.now().getEpochSecond() / 60;
    windows.entrySet().removeIf(e -> e.getValue().minute() != minute);
    var w = windows.get(subject);
    int n = w == null ? 0 : w.count();
    if (n >= max || windows.size() >= 10000 && !windows.containsKey(subject))
      throw new ResponseStatusException(
          HttpStatus.TOO_MANY_REQUESTS, "Limite de consultas atingido. Aguarde um minuto.");
    windows.put(subject, new Window(minute, n + 1));
  }
}
