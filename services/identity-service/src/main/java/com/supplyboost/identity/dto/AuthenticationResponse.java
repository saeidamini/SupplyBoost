package com.supplyboost.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

  private String accessToken;
  private String tokenType;
  private Long expiresIn;
  private UserResponse user;
}
