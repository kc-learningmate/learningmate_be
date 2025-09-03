package org.kc5.learningmate.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return memberRepository.findMemberDetailByEmail(username)
                               .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));

    }
}
