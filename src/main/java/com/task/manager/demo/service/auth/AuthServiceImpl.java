package com.task.manager.demo.service.auth;

import com.task.manager.demo.dto.auth.LoginRequest;
import com.task.manager.demo.dto.auth.LoginResponse;
import com.task.manager.demo.dto.auth.RegisterRequest;
import com.task.manager.demo.entity.Role;
import com.task.manager.demo.entity.User;
import com.task.manager.demo.exception.BadRequestException;
import com.task.manager.demo.repository.RoleRepository;
import com.task.manager.demo.repository.UserRepository;
import com.task.manager.demo.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link AuthService} interface that provides
 * authentication and user registration functionalities using Spring Security.
 * <p>
 * This service handles user login, token generation, and registration of new
 * accounts, validating credentials and assigning roles based on the given
 * request data.
 * </p>
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new {@code AuthServiceImpl} with the required dependencies.
     *
     * @param authenticationManager the Spring Security authentication manager
     * @param tokenProvider         utility for generating and parsing JWT tokens
     * @param userRepository        repository for user persistence
     * @param roleRepository        repository for role retrieval
     * @param passwordEncoder       encoder for encrypting user passwords
     */
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user based on the given {@link LoginRequest}.
     * <p>
     * Validates email and password fields, attempts authentication using
     * Spring Security, and generates a JWT token if the credentials are valid.
     * </p>
     *
     * @param request the login request containing email and password
     * @return a {@link LoginResponse} containing the JWT token, expiration time,
     *         and assigned roles
     * @throws IllegalArgumentException if email or password is missing
     * @throws BadCredentialsException  if authentication fails due to invalid credentials
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.email() == null || request.email().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }
        if (request.password() == null || request.password().isBlank()){
            throw new IllegalArgumentException("Password is required");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            List<String> roles = tokenProvider.getRolesFromToken(token);
            long expiresIn = 86400000L; // 24 horas

            return new LoginResponse(token, expiresIn, roles);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials, try again");
        }
    }

    /**
     * Registers a new user based on the provided {@link RegisterRequest}.
     * <p>
     * Validates required fields, ensures email uniqueness, assigns roles,
     * encrypts the password, and saves the user. After saving, the method
     * authenticates the new user and returns a JWT token.
     * </p>
     *
     * @param request the registration request containing name, email,
     *                password, and optional role list
     * @return a {@link LoginResponse} containing the JWT token, expiration time,
     *         and assigned roles of the newly registered user
     * @throws IllegalArgumentException if email or name is missing
     * @throws BadRequestException      if the email is already taken or if a provided role does not exist
     */
    @Override
    public LoginResponse register(RegisterRequest request) {
        if (request.email() == null || request.email().isBlank()){
            throw new IllegalArgumentException("Email is required");
        }
        if (request.name() == null || request.name().isBlank()){
            throw new IllegalArgumentException("Name is required");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(new HashSet<>())
                .build();

        List<String> roleNames = request.roleNames();
        if (roleNames == null || roleNames.isEmpty()) {
            roleNames = List.of("USER");
        }

        Set<Role> userRoles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName.toUpperCase())
                    .orElseThrow(() -> new BadRequestException("Role not supported: " + roleName));
            userRoles.add(role);
        }

        user.setRoles(userRoles);
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = tokenProvider.generateToken(authentication);
        List<String> roles = tokenProvider.getRolesFromToken(token);
        long expiresIn = 86400000L; // 24 horas

        return new LoginResponse(token, expiresIn, roles);
    }
}


