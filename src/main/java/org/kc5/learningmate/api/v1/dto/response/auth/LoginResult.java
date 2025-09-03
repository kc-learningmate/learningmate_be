package org.kc5.learningmate.api.v1.dto.response.auth;

import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;

public record LoginResult(String accessToken, String refreshToken, MemberResponse memberResponse) {
}
