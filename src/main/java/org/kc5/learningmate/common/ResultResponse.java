package org.kc5.learningmate.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public class ResultResponse<T> implements Serializable {
    @Schema(description = "상태 코드", example = "200")
    private final int status;
    @Schema(description = "기본 메세지", example = "success")
    private final String message;

    @Schema(description = "반환 data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public ResultResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public ResultResponse() {
        this.status = HttpStatus.OK.value();
        this.message = "success";
    }

    public ResultResponse(HttpStatus status, T result) {
        this.status = status.value();
        this.message = "success";
        this.result = result;
    }

    public ResultResponse(HttpStatus status) {
        this.status = status.value();
        this.message = "success";
    }

    public ResultResponse(T result) {
        this.status = HttpStatus.OK.value();
        this.message = "success";
        this.result = result;
    }

}

