# Build stage
FROM gradle:8.11-jdk21 AS builder

WORKDIR /app

# Copy gradle files first for better caching
COPY gradle /app/gradle
COPY gradlew build.gradle.kts settings.gradle.kts /app/
COPY buildSrc /app/buildSrc

# Copy source code
COPY example /app/example

# Build the application
ARG GRADLE_OPTS="-Xmx1g"
RUN ./gradlew :example:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar
COPY --from=builder /app/example/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
