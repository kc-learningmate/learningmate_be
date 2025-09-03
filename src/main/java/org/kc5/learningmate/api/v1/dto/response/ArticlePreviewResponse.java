package org.kc5.learningmate.api.v1.dto.response;

import java.time.LocalDateTime;

/**
 * DTO for {@link org.kc5.learningmate.domain.article.entity.Article}
 */
public record ArticlePreviewResponse(Long id,
                                     String title,
                                     String content,
                                     LocalDateTime publishedAt,
                                     String press) {
}