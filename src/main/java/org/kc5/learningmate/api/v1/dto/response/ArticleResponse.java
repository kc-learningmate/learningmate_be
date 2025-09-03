package org.kc5.learningmate.api.v1.dto.response;

import lombok.Builder;
import lombok.Value;
import org.kc5.learningmate.domain.article.entity.Article;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link org.kc5.learningmate.domain.article.entity.Article}
 */
@Value
@Builder
public class ArticleResponse implements Serializable {
    Long id;
    String title;
    String content;
    String link;
    String reporter;
    LocalDateTime publishedAt;
    String press;
    String summary;
    Long views;
    //    TODO: 속성 추가 필요. Long scrapCount;
    
    public static ArticleResponse from(Article article) {
        return ArticleResponse.builder()
                              .id(article.getId())
                              .title(article.getTitle())
                              .content(article.getContent())
                              .link(article.getLink())
                              .reporter(article.getReporter())
                              .publishedAt(article.getPublishedAt())
                              .press(article.getPress())
                              .summary(article.getSummary())
                              .views(article.getViews())
                              .build();
    }
}