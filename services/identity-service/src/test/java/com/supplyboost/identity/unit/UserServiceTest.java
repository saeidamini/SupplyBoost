package com.supplyboost.identity.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.supplyboost.identity.domain.Role;
import com.supplyboost.identity.domain.User;
import com.supplyboost.identity.dto.UserRegistrationRequest;
import com.supplyboost.identity.dto.UserResponse;
import com.supplyboost.identity.exception.UserAlreadyExistsException;
import com.supplyboost.identity.repository.RoleRepository;
import com.supplyboost.identity.repository.UserRepository;
import com.supplyboost.identity.service.JwtTokenService;
import com.supplyboost.identity.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtTokenService jwtTokenService;

  @InjectMocks private UserService userService;

  private UserRegistrationRequest registrationRequest;
  private Role userRole;

  @BeforeEach
  void setUp() {
    registrationRequest =
        UserRegistrationRequest.builder()
            .email("test@example.com")
            .username("testuser")
            .password("SecurePass123!")
            .firstName("Test")
            .lastName("User")
            .phoneNumber("+1234567890")
            .build();

    userRole = Role.builder().id(1L).name("ROLE_USER").description("Standard user role").build();
  }

  @Test
  void shouldRegisterUserSuccessfully() {
    when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
    when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UserResponse response = userService.registerUser(registrationRequest);

    assertThat(response).isNotNull();
    assertThat(response.getEmail()).isEqualTo(registrationRequest.getEmail());
    assertThat(response.getUsername()).isEqualTo(registrationRequest.getUsername());
    assertThat(response.getFirstName()).isEqualTo(registrationRequest.getFirstName());
    assertThat(response.getLastName()).isEqualTo(registrationRequest.getLastName());
  }

  @Test
  void shouldThrowExceptionWhenUsernameAlreadyExists() {
    when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(true);

    assertThatThrownBy(() -> userService.registerUser(registrationRequest))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessageContaining("Username")
        .hasMessageContaining("already taken");
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.registerUser(registrationRequest))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessageContaining("Email")
        .hasMessageContaining("already taken");
  }
}
