package org.kc5.learningmate.domain.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.kc5.learningmate.common.BaseEntity;
import org.kc5.learningmate.domain.member.entity.Member;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        name = "likereview",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_like_review_review_member",
                        columnNames = {"review_id", "member_id"}
                )
        },
        indexes = {
                @Index(name = "idx_like_review_review", columnList = "review_id"),
                @Index(name = "idx_like_review_member", columnList = "member_id")
        }
)
public class LikeReview extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}