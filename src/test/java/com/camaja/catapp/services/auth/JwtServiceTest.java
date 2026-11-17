package com.camaja.catapp.services.auth;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.Company;
import com.camaja.catapp.models.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Test-only Base64 key (64 decoded bytes — well above HS256 minimum)
    private static final String TEST_SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RzLXRoaXMtaXMtbG9uZy1lbm91Z2gtZm9yLUhTMjU2";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 120_000L);
    }

    @Test
    void generateToken_returnsNonNullNonEmptyString() {
        User user = userWithUsername("alice");
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
    }

    @Test
    void isTokenValid_forFreshToken_returnsTrue() {
        User user = userWithUsername("alice");
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        User user = userWithUsername("bob");
        String token = jwtService.generateToken(user);
        assertThat(jwtService.extractUsername(token)).isEqualTo("bob");
    }

    @Test
    void isTokenValid_afterExpiry_throwsExpiredJwtException() throws InterruptedException {
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(shortLivedService, "jwtExpiration", 1L);
        ReflectionTestUtils.setField(shortLivedService, "refreshExpiration", 1L);

        User user = userWithUsername("charlie");
        String token = shortLivedService.generateToken(user);
        Thread.sleep(10);

        // jjwt throws ExpiredJwtException during claim parsing; isTokenValid does not catch it
        assertThatThrownBy(() -> shortLivedService.isTokenValid(token, user))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    void generateToken_forUserWithCompanyAndRole_includesClaimsInToken() {
        Company company = new Company();
        company.setName("ACME");
        // idCompany is set via setter (GeneratedValue only matters in DB; here it's in-memory)
        ReflectionTestUtils.setField(company, "idCompany", 42L);

        AccessLevel level = new AccessLevel();
        level.setName("ADMIN");

        User user = userWithUsername("admin");
        user.setCompany(company);
        user.setAccessLevel(level);

        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractClaim(token, c -> c);
        assertThat(claims.get("companyId", Long.class)).isEqualTo(42L);
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    private User userWithUsername(String username) {
        return new User(username, username + "@test.com", "password");
    }
}
