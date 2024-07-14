package com.git.polling.service;

import com.git.polling.client.GitPollingClient;
import com.git.polling.exception.ApiLimitException;
import com.git.polling.model.Ordering;
import com.git.polling.model.PageWrapper;
import com.git.polling.model.Repo;
import com.git.polling.model.SearchRequestParams;
import com.git.polling.model.SearchResultDto;
import com.git.polling.model.Sorting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitRepoScoring {

    private final static int API_RESULT_LIMITATION = 1000;

    private final GitPollingClient gitPollingClient;

    public PageWrapper scoring(SearchRequestParams sRParams) {

        SearchResultDto starRepos = getFetchRepos(sRParams.language(), sRParams.createdDate(), 1, 1,
                Sorting.START, Ordering.DESC);

        long totalCountOfRepos = starRepos.getTotalCount();
        log.info("Total count of repos: {}", totalCountOfRepos);


        long maxAllowedPages = (long) Math.ceil((double) API_RESULT_LIMITATION / sRParams.perPage());
        log.info("Max allowed pages: {}", maxAllowedPages);

        if (sRParams.page() > maxAllowedPages) {
            throw new ApiLimitException("page number is greater than last page number: " + maxAllowedPages);
        }

        double realPagesCount = Math.ceil((double) totalCountOfRepos / sRParams.perPage());
        long pagesCount = Math.min((long) realPagesCount, maxAllowedPages);

        final long maxStars = starRepos.getItems().getFirst().getStars();
        log.info("Max starts: {}", maxStars);

        final long maxForks = getFetchRepos(sRParams.language(), sRParams.createdDate(), 1, 1,
                Sorting.FORK, Ordering.DESC)
                .getItems().getFirst().getForksCount();
        log.info("Max forks: {}", maxForks);

        final LocalDate newest = getFetchRepos(sRParams.language(), sRParams.createdDate(), 1, 1,
                Sorting.UPDATE, Ordering.DESC)
                .getItems().getFirst().getLastUpdatedAt();
        log.info("Newest update: {}", newest);

        List<Repo> repos = getFetchRepos(sRParams.language(), sRParams.createdDate(),
                sRParams.page(), sRParams.perPage(), Sorting.START, Ordering.DESC).getItems();

        return PageWrapper.builder().lastPage(pagesCount)
                .scoredRepos(ScoreCalculator.scoreRepositories(repos, maxStars, maxForks, newest))
                .nextPage(String.format(
                        "/api/public/repositories?createdDate=%s&language=%s" +
                                "&perPage=%s&page=%s",
                        sRParams.createdDate(), sRParams.language(), sRParams.perPage(), Math.min(sRParams.page() + 1, pagesCount)))
                .currentPage(sRParams.page())
                .build();
    }

    private SearchResultDto getFetchRepos(String language,
                                          LocalDate createdDate,
                                          long page,
                                          int perPage,
                                          Sorting sorting,
                                          Ordering ordering) {
        log.debug("calling git api: language: {}, createdDate: {}, page: {}, perPage:{}, sorting:{} " +
                        "ordering: {}", language, createdDate, page, perPage,
                sorting, ordering);
        return gitPollingClient.fetchRepos(language, createdDate, page, perPage, sorting, ordering);
    }
}
