package org.kc5.learningmate.api.v1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.auth.*;
import org.kc5.learningmate.api.v1.dto.response.auth.LoginResult;
import org.kc5.learningmate.api.v1.dto.response.auth.TokenResponse;
import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;
import org.kc5.learningmate.common.ResultResponse;
import org.kc5.learningmate.domain.auth.provider.HttpCookieProvider;
import org.kc5.learningmate.domain.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;
    private final HttpCookieProvider httpCookieProvider;

    @PostMapping("/sign-in")
    public ResponseEntity<ResultResponse<MemberResponse>> signInByEmailPwd(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResult loginResult = authService.signInByEmailPwd(loginRequest);

        ResponseCookie responseCookie = httpCookieProvider.generateAccessTokenCookie(loginResult.accessToken());

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                             .body(new ResultResponse<>(loginResult.memberResponse()));
    }


    @PostMapping("/sign-up")
    ResponseEntity<ResultResponse<Void>> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @GetMapping("/emails/existence")
    ResponseEntity<ResultResponse<Boolean>> checkEmailExists(@RequestParam @Email String email) {
        boolean isExist = authService.checkEmailExists(email);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK, isExist));
    }

    @PostMapping("/send-auth-code")
    ResponseEntity<ResultResponse<Void>> sendAuthCode(@Valid @RequestBody AuthCodeGetRequest authCodeGetRequest) {
        authService.sendAuthCodeMail(authCodeGetRequest.email());

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @PostMapping("/auth-code/validate")
    ResponseEntity<ResultResponse<Void>> validateAuthCode(@Valid @RequestBody AuthCodeValidateRequest authCodeValidateRequest) {
        authService.validateAuthCode(authCodeValidateRequest.email(), authCodeValidateRequest.authCode());

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @PostMapping("/sign-out")
    ResponseEntity<ResultResponse<Void>> signOut(HttpServletRequest request) {
        String refreshToken = httpCookieProvider.getRefreshToken(request);
        authService.signOut(refreshToken);

        ResponseCookie signOutAccessTokenCookie = httpCookieProvider.createSignOutCookie("accessToken");

        ResponseCookie signOutRefreshTokenCookie = httpCookieProvider.createSignOutCookie("refreshToken");

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, signOutAccessTokenCookie.toString())
                             .header(HttpHeaders.SET_COOKIE, signOutRefreshTokenCookie.toString())
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @PostMapping("/refresh-token")
    ResponseEntity<ResultResponse<Void>> refreshToken(HttpServletRequest request) {
        String refreshToken = httpCookieProvider.getRefreshToken(request);

        TokenResponse tokenResponse = authService.refreshToken(refreshToken);

        ResponseCookie accessTokenCookie = httpCookieProvider.generateAccessTokenCookie(tokenResponse.accessToken());

        ResponseCookie refreshTokenCookie = httpCookieProvider.generateRefreshTokenCookie(tokenResponse.refreshTokens());


        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                             .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @PostMapping("/passwd-resets")
    public ResponseEntity<ResultResponse<Void>> sendPasswdResetMail(@Valid @RequestBody SendResetPasswdRequest sendResetPasswdRequest) {
        authService.sendResetPasswordMail(sendResetPasswdRequest.email());
        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }

    @PatchMapping("/passwd-resets")
    public ResponseEntity<ResultResponse<Void>> resetPasswd(@Valid @RequestBody PasswdResetRequest passwdResetRequest) {
        authService.resetPassword(passwdResetRequest);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }
}
