package org.kc5.learningmate.domain.auth.provider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.properties.AuthProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HttpCookieProvider {
    private final AuthProperties authProperties;

    public ResponseCookie generateAccessTokenCookie(String accessToken) {
        long maxAge = authProperties.getExpirationMills() / 1000;
        return ResponseCookie.from("accessToken", accessToken)
                             .httpOnly(true)
                             .path("/")
                             .sameSite(authProperties.getSameSite())
                             .secure(true)
                             .maxAge(maxAge)
                             .build();
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        long maxAge = authProperties.getRefreshTokenExpirationDays() * 24 * 3600;
        return ResponseCookie.from("refreshToken", refreshToken)
                             .httpOnly(true)
                             .path("/")
                             .sameSite(authProperties.getSameSite())
                             .secure(true)
                             .maxAge(maxAge)
                             .build();
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) throw new CommonException(ErrorCode.UNAUTHORIZED);

        return Arrays.stream(cookies)
                     .filter(cookie -> "refreshToken".equals(cookie.getName()))
                     .findFirst()
                     .map(Cookie::getValue)
                     .orElseThrow(() -> new CommonException(ErrorCode.UNAUTHORIZED));
    }

    public ResponseCookie createSignOutCookie(String cookieName) {
        // 로그인할 때 보냈던 쿠키와 path, sameSite 등 속성이 모두 똑같아야지 브라우저의 쿠키가 업데이트 됨.
        return ResponseCookie.from(cookieName, "")
                             .httpOnly(true)
                             .path("/")
                             .sameSite(authProperties.getSameSite())
                             .secure(true)
                             .maxAge(0)
                             .build();
    }
}
