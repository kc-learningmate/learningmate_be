package org.kc5.learningmate.api.v1.dto.response;

import lombok.Builder;
import lombok.Value;
import org.kc5.learningmate.domain.keyword.entity.TodaysKeyword;

import java.time.LocalDate;

/**
 * DTO for {@link org.kc5.learningmate.domain.keyword.entity.TodaysKeyword}
 */
@Value
@Builder
public class TodaysKeywordResponse {
    Long id;
    KeywordResponse keyword;
    LocalDate date;

    public static TodaysKeywordResponse fromEntityWithKeyword(TodaysKeyword todaysKeyword) {
        return TodaysKeywordResponse.builder()
                                    .id(todaysKeyword.getId())
                                    .keyword(KeywordResponse.fromEntity(todaysKeyword.getKeyword()))
                                    .date(todaysKeyword.getDate())
                                    .build();
    }
}