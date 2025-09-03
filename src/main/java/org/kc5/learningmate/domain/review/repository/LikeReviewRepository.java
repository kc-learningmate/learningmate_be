package org.kc5.learningmate.domain.review.repository;

import org.kc5.learningmate.domain.review.entity.LikeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeReviewRepository extends JpaRepository<LikeReview, Long> {

    @Modifying
    @Query(value = """
      INSERT IGNORE INTO LikeReview (member_id, review_id, created_at, updated_at)
      VALUES (:memberId, :reviewId, NOW(), NOW())
    """, nativeQuery = true)
    int insertIgnore(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
       delete from LikeReview lr
       where lr.review.id = :reviewId
         and lr.member.id = :memberId
    """)
    int deleteDirect(@Param("reviewId") Long reviewId, @Param("memberId") Long memberId);

    @Query("SELECT COUNT(lr) FROM LikeReview lr WHERE lr.review.id = :reviewId")
    Long countByReviewId(@Param("reviewId") Long reviewId);
}
