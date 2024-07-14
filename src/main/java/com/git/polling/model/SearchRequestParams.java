package com.git.polling.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SearchRequestParams(String language,
                                  LocalDate createdDate,
                                  int page,
                                  int perPage) {
}
