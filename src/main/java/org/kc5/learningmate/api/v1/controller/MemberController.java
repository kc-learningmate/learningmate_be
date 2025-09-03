package org.kc5.learningmate.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.member.MemberUpdateRequest;
import org.kc5.learningmate.api.v1.dto.response.MyStudyResponse;
import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;
import org.kc5.learningmate.api.v1.dto.response.member.ProfileImageDto;
import org.kc5.learningmate.common.ResultResponse;
import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.auth.provider.HttpCookieProvider;
import org.kc5.learningmate.domain.member.service.MemberService;
import org.kc5.learningmate.domain.study.service.StudyService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final StudyService studyService;
    private final HttpCookieProvider httpCookieProvider;

    @GetMapping("/me")
    public ResponseEntity<ResultResponse<MemberResponse>> getMember(@AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();

        MemberResponse memberResponse = memberService.findMemberById(memberId);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(memberResponse));

    }

    @GetMapping("/profile-images/{imgUrl}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String imgUrl) {
        ProfileImageDto profileImageDto = memberService.getProfileImage(imgUrl);

        return ResponseEntity.ok()
                             .contentType(profileImageDto.mediaType())
                             .body(profileImageDto.image());
    }

    @PatchMapping("/me")
    public ResponseEntity<ResultResponse<MemberResponse>> updateMember(@Valid @RequestBody MemberUpdateRequest memberUpdateRequest, @AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = memberDetail.getMemberId();
        MemberResponse memberResponse = memberService.updateMember(memberId, memberUpdateRequest);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(memberResponse));
    }

    @PatchMapping("/me/profile-image")
    public ResponseEntity<ResultResponse<MemberResponse>> updateProfileImage(@AuthenticationPrincipal MemberDetail memberDetail, @RequestParam("image") MultipartFile image) {
        Long memberId = memberDetail.getMemberId();
        MemberResponse memberResponse = memberService.updateImage(memberId, image);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(memberResponse));
    }

    @Operation(summary = "나의 월별 학습 현황 조회", description = "캘린더에 사용되는 사용자 월별 학습 현황 조회 API 입니다.")
    @GetMapping("/me/study-status")
    public ResponseEntity<ResultResponse<List<MyStudyResponse>>> getMyStudyStatus(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                                  @RequestParam("year") int year,
                                                                                  @RequestParam("month") int month) {
        List<MyStudyResponse> studyResponse = studyService.getStudyStatus(memberDetail.getMemberId(), year, month);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(studyResponse));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ResultResponse<Void>> deleteMember(@AuthenticationPrincipal MemberDetail memberDetail, HttpServletRequest request) {
        Long memberId = memberDetail.getMemberId();
        String refreshToken = httpCookieProvider.getRefreshToken(request);
        memberService.deleteMember(memberId, refreshToken);

        return ResponseEntity.ok()
                             .body(new ResultResponse<>(HttpStatus.OK));
    }
}
