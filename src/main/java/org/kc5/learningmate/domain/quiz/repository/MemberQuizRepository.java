package org.kc5.learningmate.domain.quiz.repository;

import jakarta.validation.constraints.NotNull;
import org.kc5.learningmate.domain.quiz.entity.MemberQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberQuizRepository extends JpaRepository<MemberQuiz, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE MemberQuiz mq
           SET mq.memberAnswer = :memberAnswer
           , mq.updatedAt = CURRENT_TIMESTAMP
         WHERE mq.quiz.id = :quizId
           AND mq.member.id = :memberId
    """)
    void updateAnswer(Long quizId, Long memberId, String memberAnswer);

    @Query("""
      SELECT (count(mq) > 0)
        FROM MemberQuiz mq
       WHERE mq.quiz.id = :quizId
         AND mq.member.id = :memberId
    """)
    boolean existsSolved(Long quizId, Long memberId);

    @Query("""
        select count(mq)
        from MemberQuiz mq
        join mq.quiz q
        where mq.member.id = :memberId
          and q.article.id = :articleId
    """)
    long countSolvedByArticleAndMember(@Param("articleId") Long articleId,
                                       @Param("memberId") Long memberId);
}
