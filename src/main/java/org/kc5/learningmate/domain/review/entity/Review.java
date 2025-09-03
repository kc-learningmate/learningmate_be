package org.kc5.learningmate.domain.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.kc5.learningmate.common.BaseEntity;
import org.kc5.learningmate.domain.article.entity.Article;
import org.kc5.learningmate.domain.member.entity.Member;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @NotNull
    @Column(name = "content_1", nullable = false, length=2000)
    private String content1;

    @NotNull
    @Column(name = "content_2", nullable = false, length=2000)
    private String content2;

    @NotNull
    @Column(name = "content_3", nullable = false, length=2000)
    private String content3;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void update(String content1, String content2, String content3) {
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
    }

}