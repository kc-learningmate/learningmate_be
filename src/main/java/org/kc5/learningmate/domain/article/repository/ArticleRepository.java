package org.kc5.learningmate.domain.article.repository;

import org.kc5.learningmate.api.v1.dto.response.ArticlePreviewResponse;
import org.kc5.learningmate.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("select new org.kc5.learningmate.api.v1.dto.response.ArticlePreviewResponse(" +
            "a.id," +
            "a.title," +
            "a.content," +
            "a.publishedAt," +
            "a.press)" +
            "from Article a where a.keyword.id= :keywordId")
    List<ArticlePreviewResponse> findArticlePreviewByKeywordId(@Param("keywordId") Long keywordId);
}
