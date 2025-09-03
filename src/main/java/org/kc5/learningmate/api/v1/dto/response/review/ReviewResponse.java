package org.kc5.learningmate.api.v1.dto.response.review;

import jakarta.validation.constraints.NotNull;
import org.kc5.learningmate.domain.review.entity.Review;

import java.time.LocalDateTime;

/**
 * DTO for {@link org.kc5.learningmate.domain.review.entity.Review}
 */
public record ReviewResponse(Long id,
                             LocalDateTime updatedAt,
                             @NotNull String content1,
                             Long memberId) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUpdatedAt(),
                review.getContent1(),
                review.getMember()
                      .getId()
        );
    }

}