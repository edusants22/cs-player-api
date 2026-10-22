# syntax=docker/dockerfile:1
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY infra/maven-build.sh /opt/maven-build.sh
# Optional BuildKit secrets support environments with a trusted outbound proxy.
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml --mount=type=secret,id=ca_bundle --mount=type=secret,id=java_cacerts sh /opt/maven-build.sh dependency:go-offline
COPY src src
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml --mount=type=secret,id=ca_bundle --mount=type=secret,id=java_cacerts sh /opt/maven-build.sh -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/cs-player-api-0.0.1-SNAPSHOT.jar app.jar
USER 10001
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
