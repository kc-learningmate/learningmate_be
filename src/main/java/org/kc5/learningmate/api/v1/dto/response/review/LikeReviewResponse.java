package org.kc5.learningmate.api.v1.dto.response.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 좋아요 수 응답 모델")
public class LikeReviewResponse {

    @Schema(description = "리뷰 번호")
    private Long id;

    @Schema(description = "리뷰 좋아요 수")
    private Long reviewCount;

    // Entity -> DTO 변환 메서드
    public static LikeReviewResponse from(Long reviewId, Long reviewCount) {
        return LikeReviewResponse.builder()
                .id(reviewId)
                .reviewCount(reviewCount)
                .build();
    }

}
