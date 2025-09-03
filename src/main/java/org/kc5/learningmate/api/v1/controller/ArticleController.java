package org.kc5.learningmate.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.member.MemberQuizRequest;
import org.kc5.learningmate.api.v1.dto.response.ArticleResponse;
import org.kc5.learningmate.api.v1.dto.response.QuizResponse;
import org.kc5.learningmate.api.v1.dto.response.common.PageResponse;
import org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse;
import org.kc5.learningmate.common.ResultResponse;
import org.kc5.learningmate.domain.article.service.ArticleService;
import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.review.service.ReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;
    private final ReviewService reviewService;

    @Operation(summary = "기사에 대한 퀴즈 조회", description = "특정 기사에 대해 작성한 퀴즈를 조회 합니다.")
    @GetMapping("/{articleId}/quizzes")
    public ResponseEntity<ResultResponse<List<QuizResponse>>> getQuiz(@PathVariable("articleId") Long articleId) {
        List<QuizResponse> response = articleService.getQuizList(articleId);
        return ResponseEntity
                .ok(new ResultResponse<>(response));
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ResultResponse<ArticleResponse>> findArticleById(@PathVariable("articleId") Long articleId) {
        return ResponseEntity
                .ok(new ResultResponse<>(articleService.findById(articleId)));
    }

    @Operation(summary = "기사에 대한 퀴즈 풀기", description = "특정 기사에 대한 퀴즈를 풀고 채점합니다.")
    @PostMapping("/{articleId}/quizzes/{quizId}")
    public ResponseEntity<ResultResponse<QuizResponse>> solveQuiz(@PathVariable("articleId") Long articleId,
                                                                  @PathVariable("quizId") Long quizId,
                                                                  @Valid @RequestBody MemberQuizRequest req,
                                                                  @AuthenticationPrincipal MemberDetail memberDetail) {
        QuizResponse response = articleService.solveQuiz(articleId, quizId, req, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>(response));
    }

    @Operation(summary = "좋아요 수를 포함한 리뷰 목록 조회", description = "좋아요 수를 포함한 리뷰 목록을 조회 합니다.")
    @GetMapping("/{articleId}/reviews")
    public ResponseEntity<ResultResponse<PageResponse<PageReviewCountResponse>>> getReviewsByArticleId(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                                            @PathVariable("articleId") Long articleId,
                                                                                            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity
                .ok(new ResultResponse<>(reviewService.getReviewsByArticleId(memberDetail.getMemberId(), articleId, pageable)));
    }

}
