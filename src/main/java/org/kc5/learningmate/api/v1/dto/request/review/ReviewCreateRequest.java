package org.kc5.learningmate.api.v1.dto.request.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.kc5.learningmate.domain.article.entity.Article;
import org.kc5.learningmate.domain.member.entity.Member;
import org.kc5.learningmate.domain.review.entity.Review;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 작성 요청 모델")
public class ReviewCreateRequest {

    @NotBlank(message = "content1를 입력해주세요.")
    @Schema(description = "기사에 대한 내 생각",
            example = "주택 공시가격 상승이 보유세 증가로 이어지는 것은 자연스러운 현상이지만, " +
                    "실거주 1가구 1주택자에 대한 세부담 완화 논의가 필요하다. ")
    private String content1;

    @NotBlank(message = "content2를 입력해주세요.")
    @Schema(description = "어려웠던 용어 정리",
            example = "공시가격이란, 정부가 매년 1월 1일 기준으로 산정하여 발표하는 부동산의 가격")
    private String content2;

    @NotBlank(message = "content3을 입력해주세요.")
    @Schema(description = "개인적으로 더 공부한 내용",
            example = "최근 정부의 부동산 정책 변화와 세제 개편 방향")
    private String content3;

    // DTO -> Entity 변환 메서드
    public static Review from(ReviewCreateRequest request, Member member, Article article) {
        return Review.builder()
                .member(member)
                .article(article)
                .content1(request.getContent1())
                .content2(request.getContent2())
                .content3(request.getContent3())
                .build();
    }

}