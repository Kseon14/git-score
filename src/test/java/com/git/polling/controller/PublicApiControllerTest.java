package com.git.polling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.git.polling.exception.ApiLimitException;
import com.git.polling.exception.RateLimitException;
import com.git.polling.model.PageWrapper;
import com.git.polling.model.ScoringDto;
import com.git.polling.model.SearchRequestParams;
import com.git.polling.service.GitRepoScoring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PublicApiController.class)
public class PublicApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitRepoScoring gitRepoScoring;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getScoredReposSuccessfully() throws Exception {
        ScoringDto scoringDto = ScoringDto.builder()
                .repoName("test-repo")
                .repoUrl("https://github.com/test-repo")
                .score(9.55)
                .build();

        PageWrapper pageWrapper = PageWrapper.builder()
                .currentPage(1)
                .lastPage(1)
                .scoredRepos(Collections.singletonList(scoringDto))
                .nextPage(null)
                .build();

        Mockito.when(gitRepoScoring.scoring(Mockito.any(SearchRequestParams.class)))
                .thenReturn(pageWrapper);

        mockMvc.perform(get("/api/public/repositories")
                        .param("language", "Java")
                        .param("createdDate", "2023-07-14")
                        .param("page", "1")
                        .param("perPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.lastPage").value(1))
                .andExpect(jsonPath("$.scoredRepos[0].repoName").value("test-repo"))
                .andExpect(jsonPath("$.scoredRepos[0].repoUrl").value("https://github.com/test-repo"))
                .andExpect(jsonPath("$.scoredRepos[0].score").value(9.55));
    }

    @Test
    public void rateLimitExceptionThrow() throws Exception {
        Mockito.when(gitRepoScoring.scoring(Mockito.any()))
                .thenThrow(new RateLimitException("Rate limit exceeded"));

        mockMvc.perform(get("/api/public/repositories")
                        .param("language", "Java")
                        .param("createdDate", "2023-07-14"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded"))
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void apiLimitExceptionThrow() throws Exception {
        Mockito.when(gitRepoScoring.scoring(Mockito.any()))
                .thenThrow(new ApiLimitException("API limit exceeded"));

        mockMvc.perform(get("/api/public/repositories")
                        .param("language", "Java")
                        .param("createdDate", "2023-07-14"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("API limit exceeded"))
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testInternalErrorException() throws Exception {
        Mockito.when(gitRepoScoring.scoring(Mockito.any()))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get("/api/public/repositories")
                        .param("language", "Java")
                        .param("createdDate", "2023-07-14"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Internal Error"))
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}
