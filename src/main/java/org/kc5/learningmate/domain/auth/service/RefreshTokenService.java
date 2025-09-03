package org.kc5.learningmate.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.properties.AuthProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthProperties authProperties;

    public String generateRefreshToken(Long memberId) {
        String refreshToken = UUID.randomUUID()
                                  .toString();

        redisTemplate.opsForValue()
                     .set(refreshToken, memberId, authProperties.getRefreshTokenExpirationDays(), TimeUnit.DAYS);

        return refreshToken;
    }

    public Long validateRefreshToken(String refreshToken) {
        Object memberId = redisTemplate.opsForValue()
                                       .get(refreshToken);

        if (memberId == null) throw new CommonException(ErrorCode.INVALID_REFRESH_TOKEN);

        return ((Number) memberId).longValue();
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }
}
