# --- Build stage -------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cache dependencies separately from source so code-only changes don't
# re-download the internet on every build.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
# Tests need Docker-in-Docker for Testcontainers, which this build stage
# doesn't have - they're run separately in CI (see .github/workflows/ci.yml).
RUN mvn -B clean package -DskipTests

# --- Run stage -----------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
