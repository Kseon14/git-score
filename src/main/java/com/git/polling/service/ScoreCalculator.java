package com.git.polling.service;

import com.git.polling.mapper.ScoringMapper;
import com.git.polling.model.Repo;
import com.git.polling.model.ScoringDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Slf4j
@UtilityClass
public class ScoreCalculator {

    private static final double STARS_WEIGHT = 0.5;
    private static final double FORKS_WEIGHT = 0.3;
    private static final double LAST_UPDATE_WEIGHT = 0.2;
    private static final LocalDate DATE_OF_GITHUB_START = LocalDate.of(2008, 4, 1);

    private static double calculateScore(Repo repo, long maxStars, long maxForks, LocalDate maxLastUpdateDate,
                                         long maxDaysSinceUpdate) {

        double normalizedStars = (double) repo.getStars() / maxStars;
        double normalizedForks = (double) repo.getForksCount() / maxForks;

        long daysSinceUpdate = ChronoUnit.DAYS.between(repo.getLastUpdatedAt(), maxLastUpdateDate);
        double normalizedLastUpdate = 1.0 - ((double) daysSinceUpdate / maxDaysSinceUpdate);

        double score = (normalizedStars * STARS_WEIGHT) +
                (normalizedForks * FORKS_WEIGHT) +
                (normalizedLastUpdate * LAST_UPDATE_WEIGHT);

        // Scale the score to be between 0 and 10
        score = score * 10;

        // Round the score to two decimal places
        score = Math.round(score * 100.0) / 100.0;

        // Ensure the score is within the 0.0 to 10.0 range
        score = Math.max(0.0, Math.min(score, 10.0));

        return score;
    }

    public static List<ScoringDto> scoreRepositories(List<Repo> repositories,
                                                     long maxStars,
                                                     long maxForks,
                                                     LocalDate newest) {
        if (maxStars == 0 || maxForks == 0 || newest == null) {
            throw new IllegalArgumentException("max values must be greater than zero and date must be non-null");
        }
        long maxDaysSinceUpdate = ChronoUnit.DAYS.between(DATE_OF_GITHUB_START, newest);
        return repositories.stream()
                .map(repo -> {
                    double score = calculateScore(repo, maxStars, maxForks, newest, maxDaysSinceUpdate);
                    log.debug("repo: {}[{},{},{}] score: {}", repo.getRepoName(), repo.getStars(), repo.getForksCount(),
                            repo.getLastUpdatedAt(), score);
                    return ScoringMapper.toScoringDto(repo, score);
                })
                .sorted(Comparator.comparing(ScoringDto::getScore).reversed())
                .toList();
    }
}
