package org.kc5.learningmate.api.v1.dto.response.member;

import org.kc5.learningmate.domain.member.entity.Member;

public record MemberResponse(Long id, String email, String nickname, String imageUrl) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getImageUrl());
    }
}