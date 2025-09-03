package org.kc5.learningmate.api.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kc5.learningmate.api.v1.dto.request.review.ReviewCreateRequest;
import org.kc5.learningmate.api.v1.dto.request.review.ReviewUpdateRequest;
import org.kc5.learningmate.api.v1.dto.response.common.PageResponse;
import org.kc5.learningmate.api.v1.dto.response.review.LikeReviewResponse;
import org.kc5.learningmate.api.v1.dto.response.review.MyReviewResponse;
import org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse;
import org.kc5.learningmate.api.v1.dto.response.review.PageReviewResponse;
import org.kc5.learningmate.common.ResultResponse;
import org.kc5.learningmate.domain.auth.entity.MemberDetail;
import org.kc5.learningmate.domain.review.service.ReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@Tag(name = "Review Controller", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "기사 상세 리뷰 작성", description = "한 개의 기사에 대해 리뷰를 작성합니다.")
    @PostMapping("/articles/{articleId}/reviews")
    public ResponseEntity<ResultResponse<Void>> createReview(@PathVariable("articleId") Long articleId,
                                                             @Valid @RequestBody ReviewCreateRequest request,
                                                             @AuthenticationPrincipal MemberDetail memberDetail) {
        reviewService.createReview(articleId, request, memberDetail.getMemberId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResultResponse<>(HttpStatus.CREATED));
    }

    @Operation(summary = "기사 상세 내 리뷰 조회", description = "특정 기사에 대해 작성한 나의 리뷰를 조회 합니다.")
    @GetMapping("/articles/{articleId}/reviews/me")
    public ResponseEntity<ResultResponse<MyReviewResponse>> getReview(@PathVariable("articleId") Long articleId,
                                                                      @AuthenticationPrincipal MemberDetail memberDetail) {
        MyReviewResponse response = reviewService.getReview(articleId, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>(response));
    }

    @Operation(summary = "기사 상세 리뷰 수정", description = "한 개의 기사에 대해 리뷰를 수정 합니다.")
    @PatchMapping("/articles/{articleId}/reviews/{reviewId}")
    public ResponseEntity<ResultResponse<MyReviewResponse>> updateReview(@PathVariable("articleId") Long articleId,
                                                             @PathVariable("reviewId") Long reviewId,
                                                             @Valid @RequestBody ReviewUpdateRequest request,
                                                             @AuthenticationPrincipal MemberDetail memberDetail) {
        MyReviewResponse response = reviewService.updateReview(articleId, reviewId, request, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>(response));
    }

    @Operation(summary = "기사 상세 리뷰 삭제", description = "한 개의 기사에 대해 리뷰를 삭제 합니다.")
    @DeleteMapping("/articles/{articleId}/reviews/{reviewId}")
    public ResponseEntity<ResultResponse<Void>> createReview(@PathVariable("articleId") Long articleId,
                                                             @PathVariable("reviewId") Long reviewId,
                                                             @AuthenticationPrincipal MemberDetail memberDetail) {
        reviewService.deleteReview(articleId, reviewId, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>());
    }

    @Operation(summary = "리뷰 좋아요", description = "한 개의 리뷰에 대해 좋아요 합니다.")
    @PostMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<ResultResponse<Void>> likeReview(@PathVariable("reviewId") Long reviewId,
                                                           @AuthenticationPrincipal MemberDetail memberDetail) {
        reviewService.likeReview(reviewId, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>());
    }

    @Operation(summary = "리뷰 좋아요 취소", description = "한 개의 리뷰에 대해 좋아요 취소 합니다.")
    @DeleteMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<ResultResponse<Void>> unlikeReview(@PathVariable("reviewId") Long reviewId,
                                                             @AuthenticationPrincipal MemberDetail memberDetail) {
        reviewService.unlikeReview(reviewId, memberDetail.getMemberId());
        return ResponseEntity
                .ok(new ResultResponse<>());
    }

    @Operation(summary = "리뷰 좋아요 수 조회", description = "한 개의 리뷰에 대한 좋아요 수를 카운트 합니다.")
    @GetMapping("/reviews/{reviewId}/likes/count")
    public ResponseEntity<ResultResponse<LikeReviewResponse>> getReviewCount(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity
                .ok(new ResultResponse<>(reviewService.getReviewCount(reviewId)));
    }

    @Operation(summary = "나의 리뷰 목록 조회", description = "나의 리뷰 목록을 조회 합니다.")
    @GetMapping("/reviews/me")
    public ResponseEntity<ResultResponse<PageResponse<PageReviewResponse>>> getMyReviews(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                                         @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity
                .ok(new ResultResponse<>(reviewService.getMyReviews(memberDetail.getMemberId(), pageable)));
    }

    @Operation(summary = "리뷰 핫 목록 조회", description = "좋아요 많은 순으로 TOP 5 리뷰 목록을 조회 합니다.")
    @GetMapping("/hot-reviews")
    public ResponseEntity<ResultResponse<List<PageReviewCountResponse>>> getHotReviews(@AuthenticationPrincipal MemberDetail memberDetail,
                                                                                       @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity
                .ok(new ResultResponse<>(reviewService.getHotReviews(memberDetail.getMemberId(), date)));
    }

}