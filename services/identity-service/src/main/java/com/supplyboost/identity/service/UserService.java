package com.supplyboost.identity.service;

import com.supplyboost.identity.domain.Role;
import com.supplyboost.identity.domain.User;
import com.supplyboost.identity.dto.*;
import com.supplyboost.identity.exception.InvalidCredentialsException;
import com.supplyboost.identity.exception.UserAlreadyExistsException;
import com.supplyboost.identity.exception.UserNotFoundException;
import com.supplyboost.identity.repository.RoleRepository;
import com.supplyboost.identity.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  @Transactional
  public UserResponse registerUser(UserRegistrationRequest request) {
    log.info("Registering new user: {}", request.getUsername());

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new UserAlreadyExistsException(
          "Username '" + request.getUsername() + "' is already taken");
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already taken");
    }

    Role userRole =
        roleRepository
            .findByName("ROLE_USER")
            .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

    User user =
        User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phoneNumber(request.getPhoneNumber())
            .enabled(true)
            .emailVerified(false)
            .accountNonLocked(true)
            .build();

    user.addRole(userRole);

    User savedUser = userRepository.save(user);
    log.info("User registered successfully: {}", savedUser.getId());

    return mapToUserResponse(savedUser);
  }

  @Transactional
  public AuthenticationResponse authenticate(LoginRequest request) {
    log.info("Authenticating user: {}", request.getUsernameOrEmail());

    User user =
        userRepository
            .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new InvalidCredentialsException("Invalid username or password");
    }

    if (!user.getEnabled()) {
      throw new InvalidCredentialsException("Account is disabled");
    }

    if (!user.getAccountNonLocked()) {
      throw new InvalidCredentialsException("Account is locked");
    }

    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    String token = jwtTokenService.generateToken(user);
    log.info("User authenticated successfully: {}", user.getId());

    return AuthenticationResponse.builder()
        .accessToken(token)
        .tokenType("Bearer")
        .expiresIn(86400L)
        .user(mapToUserResponse(user))
        .build();
  }

  public UserResponse getUserById(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

    return mapToUserResponse(user);
  }

  public UserResponse getUserByUsername(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UserNotFoundException("User not found with username: " + username));

    return mapToUserResponse(user);
  }

  private UserResponse mapToUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phoneNumber(user.getPhoneNumber())
        .enabled(user.getEnabled())
        .emailVerified(user.getEmailVerified())
        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .lastLoginAt(user.getLastLoginAt())
        .build();
  }
}
