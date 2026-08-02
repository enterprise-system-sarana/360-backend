package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record RefreshTokenRequest(@NotBlank String refreshToken) {
}
