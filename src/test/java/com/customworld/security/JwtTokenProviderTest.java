package com.customworld.security;

import com.customworld.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "Z3V1b3J0ZXN0bG9uZ2V0c3VwZXJzZWNyZXRrZXlmb3Jqd3Roc2UxMjM0NTY3ODkwMTIzNDU2Nzg5MA==";

    @Test
    void generatedAccessTokenContainsEmailAndRole() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 120_000, 60_000);

        String token = provider.generateToken("customer@example.com", UserRole.CUSTOMER);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getEmailFromJWT(token)).isEqualTo("customer@example.com");
        assertThat(provider.getRoleFromJWT(token)).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void invalidTokenIsRejected() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 120_000, 60_000);

        assertThat(provider.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void resetTokenValidationRequiresResetType() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000, 120_000, 60_000);

        String resetToken = provider.generateResetToken("customer@example.com");
        String accessToken = provider.generateToken("customer@example.com", UserRole.CUSTOMER);

        assertThat(provider.validateResetToken(resetToken)).isTrue();
        assertThat(provider.validateResetToken(accessToken)).isFalse();
    }
}
