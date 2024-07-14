package com.git.polling.client;

import com.git.polling.model.SearchResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(value = "gitClient", url = "https://api.github.com/search/repositories")
public interface GitPollingClient {

    @GetMapping(value = "?q=created:>{createdDate}+language:{language}&sort={sort}&order={ordering}&page={page}&per_page={perPage}",
            consumes = APPLICATION_JSON_VALUE)
    SearchResultDto fetchRepos(
            @PathVariable String language,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            @PathVariable long page,
            @PathVariable int perPage,
            @PathVariable String sort,
            @PathVariable String ordering);
}