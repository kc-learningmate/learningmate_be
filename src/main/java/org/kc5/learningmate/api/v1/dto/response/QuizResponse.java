package org.kc5.learningmate.api.v1.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.kc5.learningmate.domain.quiz.entity.Quiz;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "퀴즈 조회/채점 응답 모델")
public class QuizResponse {

    @Schema(description = "퀴즈 번호")
    private Long id;

    @Schema(description = "퀴즈 문제")
    private String description;

    @Schema(description = "퀴즈 보기 1")
    private String question1;

    @Schema(description = "퀴즈 보기 2")
    private String question2;

    @Schema(description = "퀴즈 보기 3")
    private String question3;

    @Schema(description = "퀴즈 보기 4")
    private String question4;

    @Schema(description = "답")
    private String answer;

    @Schema(description = "해설")
    private String explanation;
    
    @Schema(description = "정답 여부")
    private String status;

    public static List<QuizResponse> from(List<Quiz> quizzes) {
        return quizzes.stream()
                .map(q -> QuizResponse.builder()
                        .id(q.getId())
                        .description(q.getDescription())
                        .question1(q.getQuestion1())
                        .question2(q.getQuestion2())
                        .question3(q.getQuestion3())
                        .question4(q.getQuestion4())
                        .build()
                )
                .toList();
    }

    // 퀴즈 채점용 응답
    public static QuizResponse from(Quiz quiz, boolean isCorrect, String memberAnswer) {
        return QuizResponse.builder()
                .answer(isCorrect ? quiz.getAnswer() : memberAnswer)
                .explanation(isCorrect ? quiz.getExplanation() : null )
                .status(isCorrect ? "정답" : "오답")
                .build();
    }

}
