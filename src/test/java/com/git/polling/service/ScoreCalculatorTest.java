package com.git.polling.service;

import com.git.polling.model.Repo;
import com.git.polling.model.ScoringDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreCalculatorTest {


    @Test
    public void calculateScore_ZeroMaxValues() {
        Repo repo = Repo.builder()
                .repoName("test-repo")
                .stars(50)
                .forksCount(30)
                .lastUpdatedAt(LocalDate.of(2024, 7, 1))
                .build();

        long maxStars = 0;
        long maxForks = 0;
        LocalDate maxLastUpdateDate = LocalDate.of(2024, 7, 14);

        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.scoreRepositories(List.of(repo), maxStars, maxForks, maxLastUpdateDate));
    }

    @Test
    public void calculateScore_NullMaxLastUpdateDate() {
        long maxStars = 100;
        long maxForks = 50;

        assertThrows(IllegalArgumentException.class, () ->
                ScoreCalculator.scoreRepositories(List.of(), maxStars, maxForks, null));
    }

    @ParameterizedTest
    @MethodSource("provideRepositoriesForScoring")
    public void scoreRepositories(int stars, int forks, LocalDate maxLastUpdateDate, double expectedScore) {
        Repo repo = Repo.builder()
                .repoName("repo1")
                .stars(stars)
                .forksCount(forks)
                .lastUpdatedAt(maxLastUpdateDate)
                .build();

        List<Repo> repositories = List.of(repo);
        long maxStars = 100;
        long maxForks = 50;
        LocalDate newest = LocalDate.of(2024, 7, 14);

        List<ScoringDto> scoredRepos = ScoreCalculator.scoreRepositories(repositories, maxStars, maxForks, newest);
        assertEquals(expectedScore, scoredRepos.getFirst().getScore());
    }

    static Stream<Arguments> provideRepositoriesForScoring() {
        return Stream.of(
                Arguments.of(100, 50, LocalDate.of(2024, 7, 1), 10.0),
                Arguments.of(50, 30, LocalDate.of(2024, 7, 1), 6.3),
                Arguments.of(50, 30, LocalDate.of(2024, 6, 1), 6.29)
        );
    }
}
