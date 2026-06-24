FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN addgroup --system docs && adduser --system --ingroup docs docs

COPY --from=build /workspace/target/documentation-platform-0.0.1-SNAPSHOT.jar app.jar

USER docs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
