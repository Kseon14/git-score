package com.git.polling.mapper;

import com.git.polling.model.Repo;
import com.git.polling.model.ScoringDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScoringMapper {

    public static ScoringDto toScoringDto(Repo repo, double score) {
        if (repo == null) return null;
        return ScoringDto.builder()
                .repoName(repo.getRepoName())
                .repoUrl(repo.getRepoUrl())
                .score(score)
                .build();
    }
}
