package com.git.polling.controller;

import com.git.polling.exception.ApiLimitException;
import com.git.polling.exception.RateLimitException;
import com.git.polling.model.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RateLimitException.class, ApiLimitException.class})
    protected ResponseEntity<Object> handleRateLimitExceeded(RuntimeException ex, WebRequest request) {
        log.error("Error:", ex);
        return handleExceptionInternal(
                ex,
                ErrorResponseDto.builder().message(ex.getMessage()).code(HttpStatus.FORBIDDEN.value()).build(),
                getJsonHeaders(),
                HttpStatus.FORBIDDEN,
                request);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleInternalError(RuntimeException ex, WebRequest request) {
        log.error("Error:", ex);
        return handleExceptionInternal(
                ex,
                ErrorResponseDto.builder().message("Internal Error").code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(),
                getJsonHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
