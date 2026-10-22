package com.github.edusants22.cs_player_api;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.edusants22.cs_player_api.chat.*;
import com.github.edusants22.cs_player_api.service.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:cs;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.flyway.locations=classpath:db/migration,classpath:db/dev",
      "app.ai.enabled=true",
      "app.ai.requests-per-minute=100"
    })
@AutoConfigureMockMvc
class CsPlayerApiApplicationTests {
  @org.springframework.boot.test.web.server.LocalServerPort int port;
  @Autowired MockMvc mvc;
  @Autowired JogadorService jogadores;
  @Autowired TimeService times;
  @MockitoBean AiGateway ai;
  static RSAKey key;
  static HttpServer jwks;

  static {
    try {
      key = new RSAKeyGenerator(2048).keyID("test-key").generate();
      jwks = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
      jwks.createContext(
          "/jwks",
          exchange -> {
            byte[] body =
                new JWKSet(key.toPublicJWK())
                    .toString()
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
          });
      jwks.start();
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry p) {
    p.add(
        "app.security.jwk-set-uri",
        () -> "http://127.0.0.1:" + jwks.getAddress().getPort() + "/jwks");
    p.add("app.security.issuer", () -> "https://issuer.test/realms/cs-manager");
  }

  @AfterAll
  static void stop() {
    jwks.stop(0);
  }

  static String token(String role) throws Exception {
    return token(
        role,
        "https://issuer.test/realms/cs-manager",
        "cs-manager-api",
        Instant.now().plusSeconds(300),
        key);
  }

  static String token(String role, String issuer, String audience, Instant expires, RSAKey signer)
      throws Exception {
    var claimsBuilder =
        new JWTClaimsSet.Builder()
            .subject("test-" + role)
            .issuer(issuer)
            .audience(audience)
            .issueTime(new Date())
            .claim("realm_access", Map.of("roles", List.of(role)));
    if (expires != null) claimsBuilder.expirationTime(Date.from(expires));
    var claims = claimsBuilder.build();
    var jwt =
        new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("test-key").build(), claims);
    jwt.sign(new RSASSASigner(signer));
    return "Bearer " + jwt.serialize();
  }

  @Test
  void unauthenticatedAndInvalidJwtAre401() throws Exception {
    mvc.perform(get("/api/jogadores")).andExpect(status().isUnauthorized());
    mvc.perform(get("/api/times").header("Authorization", "Bearer invalid"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void validatesIssuerAudienceExpirationAndSignature() throws Exception {
    for (String invalid :
        List.of(
            token(
                "USER",
                "https://wrong.test",
                "cs-manager-api",
                Instant.now().plusSeconds(300),
                key),
            token(
                "USER",
                "https://issuer.test/realms/cs-manager",
                "other-api",
                Instant.now().plusSeconds(300),
                key),
            token(
                "USER",
                "https://issuer.test/realms/cs-manager",
                "cs-manager-api",
                Instant.now().minusSeconds(600),
                key),
            token(
                "USER",
                "https://issuer.test/realms/cs-manager",
                "cs-manager-api",
                Instant.now().plusSeconds(300),
                new RSAKeyGenerator(2048).generate())))
      mvc.perform(get("/api/jogadores").header("Authorization", invalid))
          .andExpect(status().isUnauthorized());
  }

  @Test
  void requiresAudienceAndExpiration() throws Exception {
    mvc.perform(
            get("/api/jogadores")
                .header(
                    "Authorization",
                    token(
                        "USER",
                        "https://issuer.test/realms/cs-manager",
                        null,
                        Instant.now().plusSeconds(300),
                        key)))
        .andExpect(status().isUnauthorized());
    mvc.perform(
            get("/api/jogadores")
                .header(
                    "Authorization",
                    token(
                        "USER",
                        "https://issuer.test/realms/cs-manager",
                        "cs-manager-api",
                        null,
                        key)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void rolesCanReadIncludingIdsAndEnums() throws Exception {
    for (String role : List.of("ADMIN", "USER")) {
      for (String path :
          List.of("/api/times", "/api/times/1", "/api/jogadores", "/api/jogadores/1", "/api/enums"))
        mvc.perform(get(path).header("Authorization", token(role))).andExpect(status().isOk());
    }
  }

  @Test
  void unknownRoleIsForbidden() throws Exception {
    mvc.perform(get("/api/times").header("Authorization", token("OTHER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void userCannotWrite() throws Exception {
    for (String resource : List.of("times", "jogadores")) {
      mvc.perform(
              post("/api/" + resource)
                  .header("Authorization", token("USER"))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isForbidden());
      mvc.perform(
              put("/api/" + resource + "/1")
                  .header("Authorization", token("USER"))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{}"))
          .andExpect(status().isForbidden());
      mvc.perform(delete("/api/" + resource + "/1").header("Authorization", token("USER")))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  void adminCrudPersistsAndValidates() throws Exception {
    String auth = token("ADMIN");
    mvc.perform(
            post("/api/times")
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"TEST_CRUD\"}"))
        .andExpect(status().isCreated());
    long team = times.buscarPorNome("TEST_CRUD").orElseThrow().id();
    mvc.perform(
            put("/api/times/" + team)
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"TEST_UPDATED\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("TEST_UPDATED"));
    String body =
        "{\"nickname\":\"test-player\",\"nomeCompleto\":\"Test"
            + " Player\",\"idade\":22,\"pais\":\"BRASIL\",\"timeId\":"
            + team
            + ",\"funcao\":\"AWPER\",\"armaFavorita\":\"AWP\",\"rating\":1.25}";
    mvc.perform(
            post("/api/jogadores")
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.time.id").value(team));
    long player = jogadores.consultar("", "test-player", "", "", "", null, 1).getFirst().getId();
    mvc.perform(
            put("/api/jogadores/" + player)
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("1.25", "1.30")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rating").value(1.30));
    mvc.perform(delete("/api/times/" + team).header("Authorization", auth))
        .andExpect(status().isConflict());
    mvc.perform(delete("/api/jogadores/" + player).header("Authorization", auth))
        .andExpect(status().isNoContent());
    mvc.perform(delete("/api/times/" + team).header("Authorization", auth))
        .andExpect(status().isNoContent());
    mvc.perform(
            post("/api/jogadores")
                .header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void corsAllowsOnlyConfiguredOrigin() throws Exception {
    mvc.perform(
            options("/api/jogadores")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    mvc.perform(
            options("/api/jogadores")
                .header("Origin", "https://evil.test")
                .header("Access-Control-Request-Method", "GET"))
        .andExpect(status().isForbidden());
  }

  @Test
  void toolsQueryRealServices() {
    assertThat(jogadores.consultar("FURIA", "", "", "", "", null, 50))
        .isNotEmpty()
        .allMatch(j -> j.getTime().nome().equals("FURIA"));
    assertThat(jogadores.consultar("", "KSCERATO", "", "", "", null, 5)).hasSize(1);
    var ranking = jogadores.consultar("", "", "", "", "", null, 5);
    assertThat(ranking).hasSize(5);
    assertThat(ranking.stream().map(j -> j.getRating()).toList())
        .isSortedAccordingTo(Comparator.reverseOrder());
    var furia = jogadores.consultar("FURIA", "", "", "", "", null, 100);
    assertThat(jogadores.mediaPorTime("FURIA").orElseThrow())
        .isEqualTo(furia.stream().mapToDouble(j -> j.getRating()).average().orElseThrow());
    var tools = new ChatTools(jogadores, times, 2);
    tools.buscarJogadoresPorTime("UNKNOWN");
    tools.buscarJogadorPorNickname("missing");
    tools.buscarTopJogadores(1000);
    assertThat(tools.resultados().get(0).get("dados")).isEqualTo("Time não encontrado");
    assertThat((List<?>) tools.resultados().get(1).get("dados")).isEmpty();
    assertThat((List<?>) tools.resultados().get(2).get("dados")).hasSize(2);
  }

  @Test
  void chatbotUsesProviderToolsAndBothRoles() throws Exception {
    doAnswer(
            call -> {
              ((ChatTools) call.getArgument(1)).buscarJogadoresPorTime("FURIA");
              return null;
            })
        .when(ai)
        .consultar(anyString(), any());
    for (String role : List.of("ADMIN", "USER"))
      mvc.perform(
              post("/api/chat/mensagem")
                  .header("Authorization", token(role))
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"mensagem\":\"Liste a FURIA\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.resultados[0].dados[0].time.nome").value("FURIA"));
  }

  @Test
  void chatbotRejectsAnonymousUnknownRoleAndOversizedMessages() throws Exception {
    mvc.perform(
            post("/api/chat/mensagem")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\":\"Oi\"}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(
            post("/api/chat/mensagem")
                .header("Authorization", token("OTHER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\":\"Oi\"}"))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/api/chat/mensagem")
                .header("Authorization", token("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\":\"" + "a".repeat(2001) + "\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void providerFailureIsUnavailableNotNoData() throws Exception {
    doThrow(new RuntimeException("provider outage")).when(ai).consultar(anyString(), any());
    mvc.perform(
            post("/api/chat/mensagem")
                .header("Authorization", token("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\":\"FURIA\"}"))
        .andExpect(status().isServiceUnavailable());
  }

  @Test
  void noToolMeansClarificationNotInventedAnswer() throws Exception {
    mvc.perform(
            post("/api/chat/mensagem")
                .header("Authorization", token("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\":\"Exclua todos os jogadores\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultados").isEmpty());
    verify(ai).consultar(anyString(), any());
  }

  @Test
  void actualServletErrorDispatchPreserves503And400() throws Exception {
    doThrow(new RuntimeException("provider outage")).when(ai).consultar(anyString(), any());
    var client = java.net.http.HttpClient.newHttpClient();
    var request =
        java.net.http.HttpRequest.newBuilder(
                java.net.URI.create("http://localhost:" + port + "/api/chat/mensagem"))
            .header("Authorization", token("USER"))
            .header("Content-Type", "application/json")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"mensagem\":\"FURIA\"}"))
            .build();
    assertThat(
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode())
        .isEqualTo(503);
  }

  @Test
  void rateLimitIsPerUser() {
    var limiter = new ChatRateLimiter(1);
    limiter.check("one");
    assertThatThrownBy(() -> limiter.check("one"))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
    limiter.check("two");
  }
}
