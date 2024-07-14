package com.git.polling.exception;

import lombok.Getter;

@Getter
public class ApiLimitException extends IllegalArgumentException {

    public ApiLimitException(String s) {
        super(s);
    }
}