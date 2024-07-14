FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY build/libs/your-app-name.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "git-scoring.jar"]
