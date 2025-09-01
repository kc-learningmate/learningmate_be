package org.kc5.learningmate.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.kc5.learningmate.common.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", unique = true, columnDefinition = "VARCHAR(50) COLLATE utf8mb4_bin")
    private String nickname;

    @Column(name = "password_hash", length = 60)
    private String passwordHash;

    @Column(name = "image_url")
    private String imageUrl;

    @ColumnDefault("1")
    @Column(nullable = false)
    private Boolean status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.passwordHash = password;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}