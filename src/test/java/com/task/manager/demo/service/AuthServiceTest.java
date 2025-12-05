package com.task.manager.demo.service;

import com.task.manager.demo.dto.auth.LoginRequest;
import com.task.manager.demo.dto.auth.LoginResponse;
import com.task.manager.demo.dto.auth.RegisterRequest;
import com.task.manager.demo.entity.Role;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.repository.RoleRepository;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.security.jwt.JwtTokenProvider;
import com.task.manager.demo.service.auth.AuthService;
import com.task.manager.demo.service.auth.AuthServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private UUID id;
    private Role oldRole;
    private User oldUser;
    private RegisterRequest oldRegisterRequest;
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl service;


    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        oldRole = new Role();
        oldRole.setId(1L);
        oldRole.setName("ROLE_ADMIN");

        oldUser = new User();
        oldUser.setId(id);
        oldUser.setName("Example user");
        oldUser.setPassword("MySecurePassword");
        oldUser.setRoles(Set.of(oldRole));

        oldRegisterRequest = new RegisterRequest(
                "Example user",
                "example@email.com",
                "MySecurePassword",
                List.of("ADMIN")
        );
    }

    @AfterEach
    public void tearDown() {
        id = null;
        oldUser = null;
        oldRole = null;
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUser() {

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        when(userRepository.findByEmail(oldRegisterRequest.email())).thenReturn(Optional.empty());
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(oldRegisterRequest.password())).thenReturn("encodedPass");
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(tokenProvider.generateToken(authMock)).thenReturn("token");
        when(tokenProvider.getRolesFromToken("token")).thenReturn(List.of("ADMIN"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse result = service.register(oldRegisterRequest);

        assertNotNull(result);
        assertEquals("token", result.token());
        assertEquals(86400000L, result.expiresIn());
        assertEquals(List.of("ADMIN"), result.roles());

        verify(userRepository).findByEmail(oldRegisterRequest.email());
        verify(roleRepository).findByName("ADMIN");
        verify(passwordEncoder).encode(oldRegisterRequest.password());
        verify(userRepository).save(any(User.class));
        verify(authenticationManager).authenticate(any());
        verify(tokenProvider).generateToken(authMock);
    }

    @Test
    @DisplayName("Should not register user with email already in use")
    void shouldNotRegisterUserWithEmailInUse() {
        when(userRepository.findByEmail(oldRegisterRequest.email()))
                .thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.register(oldRegisterRequest));

        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository).findByEmail(oldRegisterRequest.email());
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should not register user with blank email")
    void shouldNotRegisterUserWithBlankEmail() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "",
                "123456",
                List.of()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.register(request));

        assertEquals("Email is required", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should not register user with blank name")
    void shouldNotRegisterUserWithBlankName() {
        RegisterRequest request = new RegisterRequest(
                "",
                "test@email.com",
                "123456",
                List.of()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.register(request));

        assertEquals("Name is required", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }


    @Test
    @DisplayName("Should assign with USER role by default")
    void shouldAssignRoleByDefault() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@mail.com",
                "123456",
                List.of()
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(Role.builder().name("USER").build()));
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(tokenProvider.generateToken(authMock)).thenReturn("token");
        when(tokenProvider.getRolesFromToken("token")).thenReturn(List.of("USER"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse result = service.register(request);

        assertNotNull(result);
        assertEquals("token", result.token());
        assertEquals(86400000L, result.expiresIn());
        assertEquals(List.of("USER"), result.roles());

        verify(userRepository).findByEmail(request.email());
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
        verify(authenticationManager).authenticate(any());
        verify(tokenProvider).generateToken(authMock);
    }

    @Test
    @DisplayName("Should return error when a non-existing role is sent")
    void shouldReturnErrorWhenIsSendFalseRole() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@mail.com",
                "123456",
                List.of("TEST ROLE")
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(roleRepository.findByName("TEST ROLE")).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.register(request));

        assertEquals("Role not supported: TEST ROLE", exception.getMessage());
        verify(userRepository).findByEmail(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(roleRepository).findByName("TEST ROLE");
        verify(userRepository, never()).save(any());
        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should login successfully and return token")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("test@mail.com", "123456");
        Authentication authMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(tokenProvider.generateToken(authMock)).thenReturn("token");
        when(tokenProvider.getRolesFromToken("token")).thenReturn(List.of("USER"));

        LoginResponse result = service.login(request);

        assertNotNull(result);
        assertEquals("token", result.token());
        assertEquals(86400000L, result.expiresIn());
        assertEquals(List.of("USER"), result.roles());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authMock);
        verify(tokenProvider).getRolesFromToken("token");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid credentials")
    void shouldThrowWhenInvalidCredentials() {
        LoginRequest request = new LoginRequest("test@mail.com", "wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException(""));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> service.login(request));

        assertEquals("Invalid credentials, try again", exception.getMessage());

        verify(authenticationManager).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
        verify(tokenProvider, never()).getRolesFromToken(any());
    }

    @Test
    @DisplayName("Should not register user with blank name")
    void shouldNotLoginUserWithBlankPassword() {
        LoginRequest request = new LoginRequest(
                "test@email.com",
                ""
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.login(request));

        assertEquals("Password is required", exception.getMessage());

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should not register user with blank name")
    void shouldNotLoginUserWithBlankEmail() {
        LoginRequest request = new LoginRequest(
                "",
                "123456"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.login(request));

        assertEquals("Email is required", exception.getMessage());

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenProvider, never()).generateToken(any());
    }

}
