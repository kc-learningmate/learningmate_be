package org.kc5.learningmate.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.auth.LoginRequest;
import org.kc5.learningmate.api.v1.dto.request.auth.PasswdResetRequest;
import org.kc5.learningmate.api.v1.dto.request.auth.SignUpRequest;
import org.kc5.learningmate.api.v1.dto.response.auth.LoginResult;
import org.kc5.learningmate.api.v1.dto.response.auth.TokenResponse;
import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.provider.JwtTokenProvider;
import org.kc5.learningmate.domain.member.service.MemberService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailService emailService;

    public LoginResult signInByEmailPwd(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()));

            String email = authentication.getName();
            MemberResponse memberResponse = memberService.findMemberByEmail(email);

            String accessToken = jwtTokenProvider.generateToken(memberResponse.id());
            String refreshToken = refreshTokenService.generateRefreshToken(memberResponse.id());

            return new LoginResult(accessToken, refreshToken, memberResponse);

        } catch (Exception e) {
            throw new CommonException(ErrorCode.UNAUTHORIZED);
        }

    }

    public void signOut(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public TokenResponse refreshToken(String refreshToken) {
        Long memberId = refreshTokenService.validateRefreshToken(refreshToken);

        String accessToken = jwtTokenProvider.generateToken(memberId);
        refreshTokenService.deleteRefreshToken(refreshToken);
        String newRefreshToken = refreshTokenService.generateRefreshToken(memberId);

        return new TokenResponse(accessToken, newRefreshToken);
    }

    // 회원가입 관련 메서드들
    public void signUp(SignUpRequest signUpRequest) {
        validateAuthCode(signUpRequest.email(), signUpRequest.authCode());
        memberService.createMember(signUpRequest);
    }

    public boolean checkEmailExists(String email) {
        return memberService.checkEmailExists(email);
    }

    public void sendAuthCodeMail(String email) {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(1000000);
        String authCode = String.format("%06d", randomNumber);
        stringRedisTemplate.opsForValue()
                           .set(email, authCode, 3, TimeUnit.MINUTES);
        emailService.sendAuthCodeMail(email, authCode);
    }

    public void validateAuthCode(String email, String authCode) {
        String code = stringRedisTemplate.opsForValue()
                                         .get(email);

        if (!Objects.equals(code, authCode)) {
            throw new CommonException(ErrorCode.AUTH_CODE_INVALID);
        }
    }

    //비밀번호 찾기 및 재설정 관련 메서드들
    public void sendResetPasswordMail(String email) {
        if (!memberService.checkEmailExists(email)) {
            throw new CommonException(ErrorCode.EMAIL_NOT_FOUND);
        }
        String authToken = UUID.randomUUID()
                               .toString();
        stringRedisTemplate.opsForValue()
                           .set(authToken, email, 10, TimeUnit.MINUTES);

        emailService.sendResetPasswdMail(email, authToken);
    }

    public void resetPassword(PasswdResetRequest passwdResetRequest) {
        String email = stringRedisTemplate.opsForValue()
                                          .getAndDelete(passwdResetRequest.authToken());
        if (email == null) {
            throw new CommonException(ErrorCode.AUTH_TOKEN_INVALID);
        }

        memberService.updatePassword(email, passwdResetRequest.password());
    }
}
