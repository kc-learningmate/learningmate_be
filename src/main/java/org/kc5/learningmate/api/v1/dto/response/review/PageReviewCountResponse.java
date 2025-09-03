package org.kc5.learningmate.api.v1.dto.response.review;

import java.time.LocalDateTime;

public record PageReviewCountResponse(
        Long id,
        Long articleId,
        Long memberId,
        LocalDateTime createdAt,
        String content1,
        String nickname,
        String title,
        Long likeCount,
        Boolean likedByMe
) {}
