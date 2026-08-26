# =========================
# 1. Build Stage
# =========================
FROM gradle:8.10-jdk21 AS builder

WORKDIR /app

# Copy Gradle files first for better Docker cache
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build Spring Boot application
RUN ./gradlew clean bootJar --no-daemon


# =========================
# 2. Runtime Stage
# =========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy generated JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Use non-root user
USER spring

# Spring Boot port
EXPOSE 8081

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]