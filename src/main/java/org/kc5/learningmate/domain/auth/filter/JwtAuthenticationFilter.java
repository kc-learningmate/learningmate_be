package org.kc5.learningmate.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.provider.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("do auth FilterInternal: ");

        String requestURI = request.getRequestURI();
        System.out.println("requestURI: " + requestURI);
        // 2. 현재 요청이 제외할 URI에 해당하는지 확인합니다.
        if (requestURI.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveTokenFromHeader(request);

        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new CommonException(ErrorCode.UNAUTHORIZED);
        }

        return Arrays.stream(cookies)
                     .filter(cookie -> "accessToken".equals(cookie.getName()))
                     .findFirst()
                     .map(Cookie::getValue)
                     .orElseThrow(() -> new CommonException(ErrorCode.UNAUTHORIZED));
    }
}
