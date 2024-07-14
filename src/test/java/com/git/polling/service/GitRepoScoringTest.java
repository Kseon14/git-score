package com.git.polling.service;

import com.git.polling.client.GitPollingClient;
import com.git.polling.exception.ApiLimitException;
import com.git.polling.model.Ordering;
import com.git.polling.model.PageWrapper;
import com.git.polling.model.Repo;
import com.git.polling.model.SearchRequestParams;
import com.git.polling.model.SearchResultDto;
import com.git.polling.model.Sorting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitRepoScoringTest {

    @Mock
    private GitPollingClient gitPollingClient;

    @InjectMocks
    private GitRepoScoring gitRepoScoring;

    private SearchRequestParams searchRequestParams;

    @BeforeEach
    public void setUp() {
        searchRequestParams = SearchRequestParams.builder()
                .language("Java")
                .createdDate(LocalDate.of(2023, 7, 14))
                .page(1)
                .perPage(100)
                .build();
    }

    @Test
    public void scoring_Success() {
        Repo repo = Repo.builder().stars(100).forksCount(50).lastUpdatedAt(LocalDate.now()).build();
        SearchResultDto searchResultDto = SearchResultDto.builder()
                .totalCount(1000)
                .items(List.of(repo))
                .build();

        when(gitPollingClient.fetchRepos(anyString(), any(LocalDate.class), anyLong(),
                anyInt(), any(Sorting.class), any(Ordering.class)))
                .thenReturn(searchResultDto);

        PageWrapper result = gitRepoScoring.scoring(searchRequestParams);

        assertEquals(1, result.getCurrentPage());
        assertEquals(10, result.getLastPage());
        assertEquals(1, result.getScoredRepos().size());
        assertEquals(10.0, result.getScoredRepos().getFirst().getScore());
    }


    @Test
    public void scoring_ThrowsApiLimitException() {
        searchRequestParams = SearchRequestParams.builder()
                .language("Java")
                .createdDate(LocalDate.now())
                .perPage(100)
                .page(11).build();

        Repo repo = Repo.builder().stars(100).forksCount(50).lastUpdatedAt(LocalDate.now()).build();
        SearchResultDto searchResultDto = SearchResultDto.builder()
                .totalCount(1000)
                .items(Collections.singletonList(repo))
                .build();

        when(gitPollingClient.fetchRepos(anyString(), any(LocalDate.class), anyLong(),
                anyInt(), any(Sorting.class), any(Ordering.class)))
                .thenReturn(searchResultDto);

        assertThrows(ApiLimitException.class, () -> gitRepoScoring.scoring(searchRequestParams));
    }

    @Test
    public void scoring_WithMaxStarsForksAndNewestDate() {
        Repo repo = Repo.builder().stars(100).forksCount(50).lastUpdatedAt(LocalDate.now()).build();
        SearchResultDto starResultDto = SearchResultDto.builder()
                .totalCount(1000)
                .items(Collections.singletonList(repo))
                .build();

        Repo forkRepo = Repo.builder().stars(100).forksCount(60).lastUpdatedAt(LocalDate.now()).build();
        SearchResultDto forkResultDto = SearchResultDto.builder()
                .totalCount(1000)
                .items(Collections.singletonList(forkRepo))
                .build();

        Repo newestRepo = Repo.builder().stars(100).forksCount(50).lastUpdatedAt(LocalDate.now()).build();
        SearchResultDto newestResultDto = SearchResultDto.builder()
                .totalCount(1000)
                .items(Collections.singletonList(newestRepo))
                .build();

        when(gitPollingClient.fetchRepos(anyString(), any(LocalDate.class), anyLong(),
                anyInt(), eq(Sorting.START), any(Ordering.class))).thenReturn(starResultDto);
        when(gitPollingClient.fetchRepos(anyString(), any(LocalDate.class), anyLong(),
                anyInt(), eq(Sorting.FORK), any(Ordering.class))).thenReturn(forkResultDto);
        when(gitPollingClient.fetchRepos(anyString(), any(LocalDate.class), anyLong(),
                anyInt(), eq(Sorting.UPDATE), any(Ordering.class))).thenReturn(newestResultDto);

        PageWrapper result = gitRepoScoring.scoring(searchRequestParams);

        assertEquals(1, result.getCurrentPage());
        assertEquals(10, result.getLastPage());
        assertEquals(1, result.getScoredRepos().size());
        assertEquals(9.5, result.getScoredRepos().getFirst().getScore());
    }
}
