package com.supplyboost.identity.controller;

import com.supplyboost.identity.dto.UserResponse;
import com.supplyboost.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
    log.info("Getting user by ID: {}", userId);
    UserResponse response = userService.getUserById(userId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/username/{username}")
  @Operation(
      summary = "Get user by username",
      description = "Retrieve user details by username")
  public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
    log.info("Getting user by username: {}", username);
    UserResponse response = userService.getUserByUsername(username);
    return ResponseEntity.ok(response);
  }
}
