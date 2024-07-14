package com.git.polling.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
public class Repo {

    @JsonProperty("name")
    private String repoName;

    @JsonProperty("html_url")
    private String repoUrl;

    @JsonProperty("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastUpdatedAt;

    @JsonProperty("forks_count")
    private long forksCount;

    @JsonProperty("stargazers_count")
    private long stars;

}
