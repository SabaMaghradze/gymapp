FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
ARG JAR=target/*.jar
WORKDIR /app
COPY --from=build /workspace/${JAR} app.jar

ENV SPRING_PROFILES_ACTIVE=docker-no-integrations
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]