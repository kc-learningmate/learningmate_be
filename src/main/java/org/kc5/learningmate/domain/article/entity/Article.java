package org.kc5.learningmate.domain.article.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.kc5.learningmate.common.BaseEntity;
import org.kc5.learningmate.domain.keyword.entity.Keyword;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Article extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "reporter", nullable = false, length = 10)
    private String reporter;

    @Column(name = "published_at", columnDefinition = "TimeStamp(6)", nullable = false)
    private LocalDateTime publishedAt;

    @Column(name = "press", nullable = false, length = 30)
    private String press;

    @Lob
    @Column(name = "summary", columnDefinition = "TEXT", nullable = false)
    private String summary;

    @ColumnDefault("'0'")
    @Column(name = "views", nullable = false)
    private Long views;

    @ColumnDefault("'0'")
    @Column(name = "scrap_count", nullable = false)
    private Long scrapCount;
}