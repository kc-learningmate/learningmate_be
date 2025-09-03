package org.kc5.learningmate.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //400
    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다."),
    AUTH_CODE_INVALID(40001, HttpStatus.BAD_REQUEST, "유효하지 않은 인증코드 입니다."),
    AUTH_TOKEN_INVALID(40002, HttpStatus.BAD_REQUEST, "유효하지 않은 인증코드 입니다."),
    DUPLICATE_NICKNAME(40003, HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다."),
    FILE_NAME_NOT_EXISTS(40004, HttpStatus.BAD_REQUEST, "파일명이 존재하지 않습니다."),
    INVALID_FILE_EXTENSION(40005, HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자 입니다."),
    PROFILE_IMG_TOO_BIG(40006, HttpStatus.BAD_REQUEST, "프로필 이미지의 사이즈는 1MB 보다 작아야 합니다."),
    INVALID_MEMBER_ID(40006, HttpStatus.BAD_REQUEST, "해당 아이디를 가진 사용자가 존재하지 않습니다."),

    //401
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_PASSWORD(40101, HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
    INVALID_PIN(40103, HttpStatus.UNAUTHORIZED, "PIN 번호가 올바르지 않습니다."),
    INVALID_AUTH_FORMAT(40104, HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 요청 형식입니다."),
    INVALID_TOKEN(40105, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(40106, HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료 됐습니다."),

    //403
    FORBIDDEN(40300, HttpStatus.FORBIDDEN, "요청 권한이 없습니다."),

    // 404
    NOT_FOUND(40400, HttpStatus.NOT_FOUND, "존재하지 않는 자원입니다."),
    MEMBER_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    ARTICLE_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "존재하지 않는 기사입니다."),
    REVIEW_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    KEYWORD_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "존재하지 않는 키워드입니다."),
    KEYWORD_LIST_NOT_FOUND(40405, HttpStatus.NOT_FOUND, "해당 기간의 키워드가 존재하지 않습니다."),
    QUIZ_LIST_NOT_FOUND(40406, HttpStatus.NOT_FOUND, "퀴즈 목록이 존재하지 않습니다."),
    VIDEO_BY_KEYWORD_ID_NOT_FOUND(40407, HttpStatus.NOT_FOUND, "해당 키워드와 연관된 영상이 존재하지 않습니다."),
    QUIZ_NOT_FOUND(40408, HttpStatus.NOT_FOUND, "퀴즈가 존재하지 않습니다."),
    ARTICLE_BY_KEYWORD_ID_NOT_FOUND(40408, HttpStatus.NOT_FOUND, "해당 키워드와 연관된 기사가 존재하지 않습니다."),
    EMAIL_NOT_FOUND(40409, HttpStatus.NOT_FOUND, "해당 이메일을 가진 사용자가 존재하지 않습니다."),

    //409
    DUPLICATE_REVIEW(40900, HttpStatus.CONFLICT, "기사에 대한 리뷰를 이미 작성했습니다."),

    // 500
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버내부 오류입니다."),
    SEND_EMAIL_FAIL(50001, HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송을 실패했습니다."),
    LOAD_IMAGE_FAIL(50002, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 로드를 실패했습니다."),
    SAVE_IMAGE_FAIL(50003, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장을 실패했습니다.");


    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
