package org.kc5.learningmate.domain.auth.entity;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
public record MemberDetail(Long id, String email, String passwordHash) implements UserDetails {
    public static MemberDetail from(Long id) {
        return MemberDetail.builder()
                           .id(id)
                           .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getMemberId() {
        return id;
    }
}
