FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

LABEL org.opencontainers.image.authors="Daniil Istomin"
LABEL org.opencontainers.image.title="telemetry-aggregator"
LABEL org.opencontainers.image.description="Telemetry aggregation service for Kafka events, SLI windows and Prometheus metrics"
LABEL org.opencontainers.image.source="https://github.com/codbid/telemetry-aggregator"

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]