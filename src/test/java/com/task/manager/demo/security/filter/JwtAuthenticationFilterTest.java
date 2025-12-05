package com.task.manager.demo.security.filter;

import com.task.manager.demo.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - Unit Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken;
    private String invalidToken;
    private String expiredToken;

    @BeforeEach
    void setUp() {
        // Generate proper secure key for HS512
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String secret = new String(secretKey.getEncoded(), StandardCharsets.UTF_8);

        // Valid token
        validToken = Jwts.builder()
                .setSubject("testuser")
                .claim("roles", List.of("USER", "ADMIN"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        // Invalid token (wrong signature)
        invalidToken = "invalid.token.signature";

        // Expired token
        expiredToken = Jwts.builder()
                .setSubject("testuser")
                .claim("roles", List.of("USER"))
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // 30 minutes ago (expired)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    @DisplayName("Should process valid JWT token and set authentication")
    void shouldProcessValidJWTTokenAndSetAuthentication() throws ServletException, IOException {
        // Mock token provider
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(tokenProvider.getRolesFromToken(validToken)).thenReturn(List.of("USER", "ADMIN"));

        // Mock request with valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify authentication was set
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertEquals(2, authentication.getAuthorities().size());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        // Verify filter chain was called
        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    @DisplayName("Should handle invalid Authorization header format")
    void shouldHandleInvalidAuthorizationHeaderFormat() throws ServletException, IOException {
        // Mock request with invalid Authorization header format
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify filter chain was called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle token with null roles")
    void shouldHandleTokenWithNullRoles() throws ServletException, IOException {
        // Mock token provider
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(tokenProvider.getRolesFromToken(validToken)).thenReturn(null);

        // Mock request with valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify authentication was set with empty authorities
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.getAuthorities().isEmpty());

        // Verify filter chain was called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle token with empty roles list")
    void shouldHandleTokenWithEmptyRolesList() throws ServletException, IOException {
        // Mock token provider
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(tokenProvider.getRolesFromToken(validToken)).thenReturn(List.of());

        // Mock request with valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify authentication was set with empty authorities
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.getAuthorities().isEmpty());

        // Verify filter chain was called
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should test getJwtFromRequest method with valid Bearer token")
    void shouldTestGetJwtFromRequestMethodWithValidBearerToken() throws Exception {
        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", HttpServletRequest.class);
        method.setAccessible(true);

        // Mock request with valid Bearer token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        // Test the method
        String result = (String) method.invoke(jwtAuthenticationFilter, request);

        assertEquals(validToken, result);
    }

    @Test
    @DisplayName("Should test getJwtFromRequest method with invalid format")
    void shouldTestGetJwtFromRequestMethodWithInvalidFormat() throws Exception {
        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", HttpServletRequest.class);
        method.setAccessible(true);

        // Mock request with invalid format
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // Test the method
        String result = (String) method.invoke(jwtAuthenticationFilter, request);

        assertNull(result);
    }

    @Test
    @DisplayName("Should test getJwtFromRequest method with missing Authorization header")
    void shouldTestGetJwtFromRequestMethodWithMissingAuthorizationHeader() throws Exception {
        // Use reflection to test private method
        java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJwtFromRequest", HttpServletRequest.class);
        method.setAccessible(true);

        // Mock request without Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);

        // Test the method
        String result = (String) method.invoke(jwtAuthenticationFilter, request);

        assertNull(result);
    }
}