package com.supplyboost.identity.controller;

import com.supplyboost.identity.dto.*;
import com.supplyboost.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Create a new user account")
  public ResponseEntity<UserResponse> register(
      @Valid @RequestBody UserRegistrationRequest request) {
    log.info("Received registration request for username: {}", request.getUsername());
    UserResponse response = userService.registerUser(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  @Operation(summary = "Authenticate user", description = "Login with username/email and password")
  public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Received login request for: {}", request.getUsernameOrEmail());
    AuthenticationResponse response = userService.authenticate(request);
    return ResponseEntity.ok(response);
  }
}
