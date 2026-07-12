# ---- Build stage: compile the app into a runnable jar ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy the pom first and pre-download dependencies (better layer caching).
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---- Run stage: small image with just a Java runtime + the jar ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Uploaded files live here (mounted as a volume in docker-compose).
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
