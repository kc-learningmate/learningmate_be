package org.kc5.learningmate.domain.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.provider.HttpCookieProvider;
import org.kc5.learningmate.domain.auth.provider.JwtTokenProvider;
import org.kc5.learningmate.domain.auth.service.RefreshTokenService;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final HttpCookieProvider httpCookieProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

        Long id = memberRepository.findIdByEmail(email)
                                  .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));


        String accessToken = jwtTokenProvider.generateToken(id);
        String refreshToken = refreshTokenService.generateRefreshToken(id);

        ResponseCookie accessTokenCookie = httpCookieProvider.generateAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = httpCookieProvider.generateRefreshTokenCookie(refreshToken);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        response.sendRedirect("http://localhost:5173/oauth-redirect");
    }
}
