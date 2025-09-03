package org.kc5.learningmate.api.v1.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import org.kc5.learningmate.domain.study.StudyBits;

import java.time.LocalDateTime;

public record MyStudyResponse(
        @Schema(description = "study ID") Long id,
        @Schema(description = "학습 키워드") Long keywordId,
        @Schema(description = "학습 상태") Byte studyStats,
        @Schema(description = "학습 상태 카운트") int studyStatusCount,
        @Schema(description = "영상 시청 완료 여부") boolean videoCompleted,
        @Schema(description = "퀴즈 풀기 완료 여부") boolean quizCompleted,
        @Schema(description = "리뷰 작성 여부") boolean reviewCompleted,
        @Schema(description = "처음 으로 공부 한 날짜") LocalDateTime createdAt,
        @Schema(description = "마지막 으로 공부 한 날짜") LocalDateTime updatedAt
) {

    public static MyStudyResponse of(Long id, Long keywordId, Byte studyStats,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        int value = studyStats == null ? 0 : studyStats;
        return new MyStudyResponse(
                id,
                keywordId,
                studyStats,
                Integer.bitCount(value),
                (value & StudyBits.VIDEO)  != 0,
                (value & StudyBits.QUIZ)   != 0,
                (value & StudyBits.REVIEW) != 0,
                createdAt,
                updatedAt
        );
    }
}
