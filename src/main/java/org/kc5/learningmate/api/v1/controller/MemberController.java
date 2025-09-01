package org.kc5.learningmate.api.v1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.member.MemberUpdateRequest;
import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;
import org.kc5.learningmate.api.v1.dto.response.member.ProfileImageDto;
import org.kc5.learningmate.common.ResultResponse;
import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.member.service.MemberService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

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
}
