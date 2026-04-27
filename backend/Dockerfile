# syntax=docker/dockerfile:1.7
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    sh -c 'for attempt in 1 2 3; do \
      mvn -B -ntp -Dmaven.test.skip=true -Dmaven.wagon.http.retryHandler.count=5 package && exit 0; \
      echo "Maven build falhou. Tentando novamente (${attempt}/3)..." >&2; \
      sleep 10; \
    done; \
    exit 1'

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*-exec.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
