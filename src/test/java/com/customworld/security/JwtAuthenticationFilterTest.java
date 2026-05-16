package com.customworld.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private final UserPrincipal userPrincipal = mock(UserPrincipal.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userPrincipal);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validBearerTokenPopulatesReactiveSecurityContext() {
        String token = "valid-token";
        String email = "vendor@example.com";
        UserDetails userDetails = User.withUsername(email)
                .password("encoded-password")
                .authorities("ROLE_VENDOR")
                .build();
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getEmailFromJWT(token)).thenReturn(email);
        when(userPrincipal.loadUserByUsername(email)).thenReturn(userDetails);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/vendor/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build()
        );
        AtomicReference<String> authenticatedName = new AtomicReference<>();
        WebFilterChain chain = ignored -> ReactiveSecurityContextHolder.getContext()
                .doOnNext(context -> authenticatedName.set(context.getAuthentication().getName()))
                .then();

        filter.filter(exchange, chain).block();

        assertThat(authenticatedName).hasValue(email);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void invalidBearerTokenContinuesWithoutAuthentication() {
        String token = "invalid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/vendor/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build()
        );
        AtomicBoolean chainReached = new AtomicBoolean(false);
        WebFilterChain chain = ignored -> {
            chainReached.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertThat(chainReached).isTrue();
        verify(userPrincipal, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void optionsRequestSkipsJwtLookup() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("/api/vendor/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ignored")
                        .build()
        );

        filter.filter(exchange, ignored -> Mono.empty()).block();

        verify(jwtTokenProvider, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
    }
}
