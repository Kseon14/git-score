package com.git.polling.controller;

import com.git.polling.model.PageWrapper;
import com.git.polling.model.SearchRequestParams;
import com.git.polling.service.GitRepoScoring;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicApiController {

    @Value("${app.fetch.perPage:100}")
    private final int defPerPage;
    private final GitRepoScoring gitRepoScoring;

    @GetMapping("repositories")
    public ResponseEntity<PageWrapper> getScoredRepos(
            @RequestParam String language,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage) {

        final var searchReqParam = SearchRequestParams.builder()
                .language(language)
                .createdDate(createdDate)
                .page(ObjectUtils.defaultIfNull(page, 1))
                .perPage(Math.min(ObjectUtils.defaultIfNull(perPage, defPerPage), defPerPage))
                .build();
        return new ResponseEntity<>(gitRepoScoring.scoring(searchReqParam), HttpStatus.OK);
    }
}
