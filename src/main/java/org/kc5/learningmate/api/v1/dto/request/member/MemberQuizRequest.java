package org.kc5.learningmate.api.v1.dto.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "퀴즈 풀이 요청 모델")
public class MemberQuizRequest {

    @NotBlank(message = "answer를 입력해주세요.")
    @Schema(description = "퀴즈 답",
            example = "1")
    private String memberAnswer;

}
