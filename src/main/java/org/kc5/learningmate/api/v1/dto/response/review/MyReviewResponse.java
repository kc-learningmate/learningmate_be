package org.kc5.learningmate.api.v1.dto.response.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.kc5.learningmate.api.v1.dto.response.ArticleResponse;
import org.kc5.learningmate.domain.review.entity.Review;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 작성 응답 모델")
public class MyReviewResponse {

    @Schema(description = "리뷰 번호")
    private Long id;

    @Schema(description = "기사 정보")
    private ArticleResponse article;

    @Schema(description = "회원 정보")
    private Long memberId;

    @Schema(description = "기사에 대한 내 생각")
    private String content1;

    @Schema(description = "어려웠던 용어 정리")
    private String content2;

    @Schema(description = "개인적으로 더 공부한 내용")
    private String content3;

    // Entity -> DTO 변환 메서드
    public static MyReviewResponse from(Review review) {
        return MyReviewResponse.builder()
            .id(review.getId())
            .article(ArticleResponse.from(review.getArticle()))
           .memberId(review.getMember().getId())
           .content1(review.getContent1())
           .content2(review.getContent2())
           .content3(review.getContent3())
           .build();
    }

}