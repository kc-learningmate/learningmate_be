package org.kc5.learningmate.domain.auth.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.auth.properties.AuthProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final AuthProperties authProperties;
    private SecretKey key;
    private long accessTokenExpiration;

    public JwtTokenProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder()
                                .decode(authProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = authProperties.getExpirationMills();
    }

    public String generateToken(Long memberId) {
        Date now = new Date();
        Date accessTokenExpirationTime = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                   .subject(memberId.toString())
                   .expiration(accessTokenExpirationTime)
                   .signWith(key)
                   .compact();

    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Long memberId = Long.parseLong(claims.getSubject());

        UserDetails principal = MemberDetail.from(memberId);

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                       .verifyWith(key)
                       .build()
                       .parseSignedClaims(accessToken)
                       .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CommonException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_TOKEN);
        }
    }
}
