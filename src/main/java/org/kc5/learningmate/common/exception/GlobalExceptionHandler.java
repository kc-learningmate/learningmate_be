package org.kc5.learningmate.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.common.ResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ResultResponse<Void>> handleWantedException(CommonException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                             .body(new ResultResponse<>(ex.getHttpStatus(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(x -> sb.append(x)
                          .append("\n"));

        return ResponseEntity.status(ex.getStatusCode())
                             .body(new ResultResponse<>(HttpStatus.BAD_REQUEST, sb.toString()
                                                                                  .trim()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResultResponse<Void>> unhandledException(Exception e, HttpServletRequest request) {
        log.error("error occur {}", request.getRequestURI(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResultResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

}