package com.git.polling.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ScoringDto {

    private String repoName;
    private String repoUrl;
    private Double score;

}
