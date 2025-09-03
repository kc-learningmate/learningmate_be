package org.kc5.learningmate.domain.review.repository;

import org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse;
import org.kc5.learningmate.api.v1.dto.response.review.PageReviewResponse;
import org.kc5.learningmate.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByMemberIdAndArticleId(Long memberId, Long articleId);

    Optional<Review> findByArticleIdAndMemberId(Long articleId, Long memberId);

    Page<Review> findByArticleId(Long articleId, Pageable pageable);

    @Query(
            value = """
            select new org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse(
                  r.id,
                  r.article.id,
                  r.member.id,
                  r.createdAt,
                  r.content1,
                  r.member.nickname,
                  r.article.title,
                  count(lr.id),
                  case when sum(case when lr.member.id = :memberId then 1 else 0 end) > 0
                       then true else false end
                )
            from Review r
            left join LikeReview lr on lr.review = r
            where r.article.keyword.id = :keywordId
            group by r.id, r.createdAt, r.content1, r.member.nickname, r.article.title
          """,
            countQuery = """
            select count(r)
            from Review r
            where r.article.keyword.id = :keywordId
            """
    )
    Page<PageReviewCountResponse> findByKeywordId(@Param("keywordId") Long keywordId, @Param("memberId") Long memberId, Pageable pageable);

    @Query(
            value = """
              select new org.kc5.learningmate.api.v1.dto.response.review.PageReviewResponse(
                r.id, r.createdAt, r.content1, r.member.nickname, r.article.title
              )
              from Review r
              where r.member.id = :memberId
            """,
            countQuery = """
              select count(r)
              from Review r
              where r.member.id = :memberId
            """
    )
    Page<PageReviewResponse> getAllByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query(
        value = """
            select new org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse(
                  r.id,
                  r.article.id,
                  r.member.id,
                  r.createdAt,
                  r.content1,
                  r.member.nickname,
                  r.article.title,
                  count(lr.id),
                  case when sum(case when lr.member.id = :memberId then 1 else 0 end) > 0
                       then true else false end
                )
            from Review r
            left join LikeReview lr on lr.review = r
            where r.article.id = :articleId
            group by r.id, r.createdAt, r.content1, r.member.nickname, r.article.title
          """,
        countQuery = """
            select count(r)
            from Review r
            where r.article.id = :articleId
            """
        )
    Page<PageReviewCountResponse> getAllByArticleId(@Param("memberId") Long memberId, @Param("articleId") Long articleId, Pageable pageable);


    @Query(
            value = """
                select new org.kc5.learningmate.api.v1.dto.response.review.PageReviewCountResponse(
                      r.id,
                      r.article.id,
                      r.member.id,
                      r.createdAt,
                      r.content1,
                      r.member.nickname,
                      r.article.title,
                      count(lr.id),
                      case when sum(case when lr.member.id = :memberId then 1 else 0 end) > 0
                           then true else false end
                    )
                from Review r
                left join LikeReview lr on lr.review = r
                where r.createdAt >= :start and r.createdAt < :end
                group by r.id, r.createdAt, r.content1, r.member.nickname, r.article.title
                order by count(lr.review.id) desc, r.createdAt desc
              """
    )
    List<PageReviewCountResponse> getHotReviews(@Param("memberId") Long memberId,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end, Pageable pageable);
}
