package org.kc5.learningmate.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.api.v1.dto.request.auth.SignUpRequest;
import org.kc5.learningmate.api.v1.dto.request.member.MemberUpdateRequest;
import org.kc5.learningmate.api.v1.dto.response.member.MemberResponse;
import org.kc5.learningmate.api.v1.dto.response.member.ProfileImageDto;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.member.entity.Member;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Transactional
    public void createMember(SignUpRequest signUpRequest) {
        String password = passwordEncoder.encode(signUpRequest.password());
        Member newMember = Member.builder()
                                 .email(signUpRequest.email())
                                 .passwordHash(password)
                                 .status(true)
                                 .build();
        memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                               .map(MemberResponse::from)
                               .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                               .map(MemberResponse::from)
                               .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public ProfileImageDto getProfileImage(String imgUrl) {
        Resource resource = imageService.getImage(imgUrl);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource.getFilename())
                                              .orElseThrow(() -> new CommonException(ErrorCode.LOAD_IMAGE_FAIL));

        return new ProfileImageDto(resource, mediaType);
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberUpdateRequest memberUpdateRequest) {
        Member member = memberRepository.findById(id)
                                        .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));

        String nickname = memberUpdateRequest.nickname();
        String password = memberUpdateRequest.password();
        if (nickname != null && !nickname.isEmpty()) {
            if (memberRepository.existsByNickname(nickname)) {
                throw new CommonException(ErrorCode.DUPLICATE_NICKNAME);
            }
            member.updateNickname(nickname);
        }

        if (password != null && !password.isEmpty()) {
            member.updatePassword(passwordEncoder.encode(password));
        }

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateImage(Long id, MultipartFile imgFile) {
        imageService.validateProfileImgSize(imgFile);

        Member member = memberRepository.findById(id)
                                        .orElseThrow(() -> new CommonException(ErrorCode.EMAIL_NOT_FOUND));

        try {
            String imgUrl = imageService.saveImage(imgFile.getInputStream(), imgFile.getOriginalFilename(), member.getImageUrl());

            member.updateImageUrl(imgUrl);

            return MemberResponse.from(member);
        } catch (IOException e) {
            log.error("getInputStream() 호출 실패: {}", e.getMessage());
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public void updatePassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                                        .orElseThrow(() -> new CommonException(ErrorCode.EMAIL_NOT_FOUND));

        member.updatePassword(passwordEncoder.encode(password));

    }


}
