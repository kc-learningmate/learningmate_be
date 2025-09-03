package org.kc5.learningmate.api.v1.dto.response.review;

import java.time.LocalDateTime;

public record PageReviewResponse(
        Long id,
        LocalDateTime createdAt,
        String content1,
        String nickname,
        String title
) {}