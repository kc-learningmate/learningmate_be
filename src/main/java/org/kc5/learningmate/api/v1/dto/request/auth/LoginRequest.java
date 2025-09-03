package org.kc5.learningmate.api.v1.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record LoginRequest(@NotNull @Email String email, @NotNull @Size(min = 8, max = 128) String password) {
}
