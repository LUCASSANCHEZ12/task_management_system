package com.task.manager.demo.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JwtTokenProvider - Unit Tests")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String jwtSecret;
    private long jwtExpirationInMs;

    @BeforeEach
    void setUp() {
        jwtSecret = "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast512BitsLongForHS512AlgorithmSecurityCompliance123456";
        jwtExpirationInMs = 86400000;
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void shouldExtractUsernameFromValidToken() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract roles from valid token")
    void shouldExtractRolesFromValidToken() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000);

        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .claim("roles", List.of("USER"))
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject token with invalid signature")
    void shouldRejectTokenWithInvalidSignature() {
        String wrongSecret = "wrongSecretKeyThatWillNotMatchTheOriginalSecret12345678901234567890123";
        SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String tokenWithWrongSignature = Jwts.builder()
                .setSubject("testuser")
                .claim("roles", List.of("USER"))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(wrongKey, SignatureAlgorithm.HS512)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(tokenWithWrongSignature);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle empty authorities in token generation")
    void shouldHandleEmptyAuthoritiesInTokenGeneration() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertNotNull(token);
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    @DisplayName("Should validate empty token as invalid")
    void shouldValidateEmptyTokenAsInvalid() {
        boolean isValid = jwtTokenProvider.validateToken("");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should validate null token as invalid")
    void shouldValidateNullTokenAsInvalid() {
        boolean isValid = jwtTokenProvider.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should filter and extract only ROLE_ prefixed authorities")
    void shouldFilterAndExtractOnlyRolePrefixedAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("SCOPE_read"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password", authorities);

        String token = jwtTokenProvider.generateToken(authentication);
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("ADMIN"));
    }
}
