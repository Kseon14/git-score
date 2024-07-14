package com.git.polling.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends IllegalArgumentException {

    public RateLimitException(String s) {
        super(s);
    }
}