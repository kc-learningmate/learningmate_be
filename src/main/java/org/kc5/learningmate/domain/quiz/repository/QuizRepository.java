package org.kc5.learningmate.domain.quiz.repository;

import org.kc5.learningmate.domain.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByArticleId(Long articleId);

    @Query("""
      select a.keyword.id
        from Quiz q
        join q.article a
       where q.id = :quizId
    """)
    Long findKeywordIdByQuizId(Long quizId);
}
