package org.kc5.learningmate.domain.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.api.v1.dto.request.review.ReviewCreateRequest;
import org.kc5.learningmate.api.v1.dto.request.review.ReviewUpdateRequest;
import org.kc5.learningmate.api.v1.dto.response.common.PageResponse;
import org.kc5.learningmate.api.v1.dto.response.review.*;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.article.entity.Article;
import org.kc5.learningmate.domain.article.repository.ArticleRepository;
import org.kc5.learningmate.domain.member.entity.Member;
import org.kc5.learningmate.domain.member.repository.MemberRepository;
import org.kc5.learningmate.domain.review.entity.Review;
import org.kc5.learningmate.domain.review.repository.LikeReviewRepository;
import org.kc5.learningmate.domain.review.repository.ReviewRepository;
import org.kc5.learningmate.domain.study.StudyBits;
import org.kc5.learningmate.domain.study.repository.StudyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final LikeReviewRepository likeReviewRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public void createReview(Long articleId, ReviewCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));

        Article article = articleRepository.findById(articleId)
                                           .orElseThrow(() -> new CommonException(ErrorCode.ARTICLE_NOT_FOUND));

        hasWrittenReview(memberId, article.getId());

        Long keywordId = article.getKeyword().getId();
        studyRepository.upsertFlag(memberId, keywordId, StudyBits.REVIEW);

        reviewRepository.save(ReviewCreateRequest.from(request, member, article));
    }

    // 사용자 리뷰 중복 확인 메서드
    @Transactional(readOnly = true)
    public void hasWrittenReview(Long memberId, Long articleId) {
        boolean exists = reviewRepository.existsByMemberIdAndArticleId(memberId, articleId);
        if (exists) {
            throw new CommonException(ErrorCode.DUPLICATE_REVIEW);
        }
    }

    @Transactional(readOnly = true)
    public MyReviewResponse getReview(Long articleId, Long memberId) {
        articleRepository.findById(articleId)
                         .orElseThrow(() -> new CommonException(ErrorCode.ARTICLE_NOT_FOUND));

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));


        return reviewRepository.findByArticleIdAndMemberId(articleId, memberId)
                .map(MyReviewResponse::from)
                .orElse(null);

    }

    @Transactional
    public MyReviewResponse updateReview(Long articleId, Long reviewId, ReviewUpdateRequest request, Long memberId) {
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new CommonException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new CommonException(ErrorCode.REVIEW_NOT_FOUND));

        articleRepository.findById(articleId)
                                           .orElseThrow(() -> new CommonException(ErrorCode.ARTICLE_NOT_FOUND));

        // 소유/매핑 검증
        if (!review.getArticle()
                   .getId()
                   .equals(articleId)){
            throw new CommonException(ErrorCode.FORBIDDEN);
        }

        review.update(request.getContent1(), request.getContent2(), request.getContent3());

        return MyReviewResponse.from(review);
    }
  

    @Transactional
    public void deleteReview(Long articleId, Long reviewId, Long memberId) {

        Article article = articleRepository.findById(articleId).orElseThrow(() ->
                new CommonException(ErrorCode.ARTICLE_NOT_FOUND));


        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() ->
                                                new CommonException(ErrorCode.REVIEW_NOT_FOUND));

        // 해당 기사에 대한 리뷰인지 확인
        if (!review.getArticle()
                   .getId()
                   .equals(article.getId())) {
            throw new CommonException(ErrorCode.FORBIDDEN);
        }

        // 해당 사용자의 리뷰인지 확인
        if (!review.getMember()
                .getId()
                .equals(memberId)){
            throw new CommonException(ErrorCode.FORBIDDEN);
        }
        reviewRepository.deleteById(reviewId);
    }
  

    @Transactional
    public void likeReview(Long reviewId, Long memberId) {

        int affected = likeReviewRepository.insertIgnore(memberId, reviewId);
        if (affected == 0) {
            log.info("이미 좋아요 상태이므로 요청 무시 - reviewId={}, memberId={}", reviewId, memberId);
        }

    }

    @Transactional
    public void unlikeReview(Long reviewId, Long memberId) {
        int deleted = likeReviewRepository.deleteDirect(reviewId, memberId);

        if (deleted == 0) log.info("이미 좋아요 취소 상태이므로 요청 무시 - reviewId={}, memberId={}", reviewId, memberId);
    }

    @Transactional(readOnly = true)
    public PageResponse<PageReviewCountResponse> getReviewsByKeywordId(Long keywordId, Long memberId, Pageable pageable) {
        Page<PageReviewCountResponse> pageReviewCount = reviewRepository.findByKeywordId(keywordId, memberId, pageable);
        return PageResponse.from(pageReviewCount);
    }

    @Transactional(readOnly = true)
    public LikeReviewResponse getReviewCount(Long reviewId) {
       Long reviewCount =  likeReviewRepository.countByReviewId(reviewId);
       return LikeReviewResponse.from(reviewId, reviewCount);
    }

    @Transactional(readOnly = true)
    public PageResponse<PageReviewResponse> getMyReviews(Long memberId, Pageable pageable) {
        Page<PageReviewResponse> pageReview = reviewRepository.getAllByMemberId(memberId, pageable);
        return PageResponse.from(pageReview);
    }

    @Transactional(readOnly = true)
    public PageResponse<PageReviewCountResponse> getReviewsByArticleId(Long memberId, Long articleId, Pageable pageable) {
        Page<PageReviewCountResponse> pageReviewCount = reviewRepository.getAllByArticleId(memberId, articleId, pageable);
        return PageResponse.from(pageReviewCount);
    }

    @Transactional(readOnly = true)
    public List<PageReviewCountResponse> getHotReviews(Long memberId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();
        return reviewRepository.getHotReviews(memberId, start, end, PageRequest.of(0, 5));
    }

}