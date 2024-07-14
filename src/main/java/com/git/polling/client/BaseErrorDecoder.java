package com.git.polling.client;

import com.git.polling.exception.ApiLimitException;
import com.git.polling.exception.RateLimitException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        if (403 == response.status()) {
            throw new RateLimitException("API rate limit exceeded");
        }
        if (response.status() == 422) {
            throw new ApiLimitException("API limitation exceeded");
        }
        return defaultErrorDecoder.decode(s, response);
    }
}
