package org.kc5.learningmate.api.v1.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthCodeValidateRequest(@Email String email,
                                      @NotNull @Pattern(regexp = "\\d{6}", message = "인증 코드는 6자리 숫자여야 합니다.") String authCode) {
}
