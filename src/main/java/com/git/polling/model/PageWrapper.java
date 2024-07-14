package com.git.polling.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class PageWrapper {
    private int currentPage;
    private long lastPage;
    private String nextPage;
    private List<ScoringDto> scoredRepos;

}
