package com.github.edusants22.cs_player_api.config;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain security(
      HttpSecurity http,
      JwtDecoder decoder,
      @org.springframework.beans.factory.annotation.Qualifier("cors")
          CorsConfigurationSource source)
      throws Exception {
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          Object access = jwt.getClaims().get("realm_access");
          if (!(access instanceof Map<?, ?> map)
              || !(map.get("roles") instanceof Collection<?> roles)) return List.of();
          return roles.stream()
              .filter(String.class::isInstance)
              .map(String.class::cast)
              .filter(r -> r.equals("ADMIN") || r.equals("USER"))
              .<org.springframework.security.core.GrantedAuthority>map(
                  r -> new SimpleGrantedAuthority("ROLE_" + r))
              .toList();
        });
    return http.csrf(c -> c.disable())
        .cors(c -> c.configurationSource(source))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR)
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/health")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/times",
                        "/api/times/**",
                        "/api/jogadores",
                        "/api/jogadores/**",
                        "/api/enums")
                    .hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/chat/mensagem")
                    .hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/times", "/api/jogadores")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/times/*", "/api/jogadores/*")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/times/*", "/api/jogadores/*")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .denyAll())
        .oauth2ResourceServer(
            o -> o.jwt(c -> c.decoder(decoder).jwtAuthenticationConverter(converter)))
        .build();
  }

  @Bean
  JwtDecoder jwtDecoder(
      @Value("${app.security.issuer}") String issuer,
      @Value("${app.security.jwk-set-uri}") String jwks,
      @Value("${app.security.audience}") String audience) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks).build();
    OAuth2TokenValidator<Jwt> aud =
        jwt ->
            jwt.getExpiresAt() != null
                    && jwt.getAudience() != null
                    && jwt.getAudience().contains(audience)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Audience inválida", null));
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), aud));
    return decoder;
  }

  @Bean
  CorsConfigurationSource cors(@Value("${app.security.origins}") String origins) {
    var c = new CorsConfiguration();
    c.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    c.setAllowCredentials(false);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", c);
    return source;
  }
}
