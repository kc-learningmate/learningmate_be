package org.kc5.learningmate.api.v1.dto.response.auth;

public record TokenResponse(String accessToken, String refreshTokens) {
}
