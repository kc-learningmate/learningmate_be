package org.kc5.learningmate.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.domain.member.entity.Member;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberOauth2Service extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String email = oAuth2User.getAttributes()
                                 .get("email")
                                 .toString();

        if (memberRepository.existsByEmail(email)) {
            return oAuth2User;
        }

        memberRepository.save(Member.builder()
                                    .email(email)
                                    .status(true)
                                    .build());

        return oAuth2User;
    }
}
