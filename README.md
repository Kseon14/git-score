# git-score

### Start

in order to start from docker container:

1. run following command:
   ```
   ./gradlew bootJar dockerBuildImage
   ```
2. run following command:
   ```
   docker run -p 9095:8080 com.git/polling:0.0.1-snapshot
   ```

### Test

Open postman and execute following request:

```
localhost:9095/api/public/repositories?createdDate=2024-02-01&language=assembly&page=9
```

 filter param | Description                  
--------------|------------------------------
 *createdDate | Date when repo was created   
 *language    | programming language of repo 

*mandatory

 pagination param | Description                                        
------------------|----------------------------------------------------
 page             | page number for results(default is 1)              
 perPage          | how many results per one response (default is 100) 

Every page contain rated repos comparing to maximum values(stars, forks, last update) in scope of whole
search result.
Page has sorted repo by score.

how calculated score example:

Repo:

    Stars: 50
    Forks: 30
    Last Updated At: 2024-07-01

Max Values:

    Max Stars: 100
    Max Forks: 50
    Max Last Update Date: 2024-07-14

Stars Normalization:

```
normalizedStars=50/100=0.5
```

Forks Normalization:

```
normalizedForks=30/50=0.6
```

LastUpdate Normalization:

```
daysSinceUpdate=ChronoUnit.DAYS.between(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 14))=13
daysBetweenMaxAndMin=ChronoUnit.DAYS.between(LocalDate.of(2008, 4, 1), LocalDate.of(2024, 7, 14))=5948
normalizedLastUpdate=1.0−(daysSinceUpdate/daysBetweenMaxAndMin)= 0.99
```

Score

```
score=(normalizedStars×0.5)+(normalizedForks×0.3)+(normalizedLastUpdate×0.2)=0.25+0.18+0.198=0.628
```

Scaling

```
score=0.628×10=6.28
```

Rounding

```
score=Math.round(6.28 * 100.0) / 100.0=6.28
```

### Limitation

- only 1000 repos can be retrived
- not more then 2 requests per min 