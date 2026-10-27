FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Cache Gradle dependencies layer first
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle/
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

# Copy source code and build executable jar (remove plain jar)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test && rm -f build/libs/*-plain.jar

FROM eclipse-temurin:21-jre-alpine

ENV TZ=Asia/Phnom_Penh
RUN apk add --no-cache tzdata curl && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
USER spring

ARG APP_PORT=3333
ENV SERVER_PORT=${APP_PORT}
EXPOSE ${APP_PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Duser.timezone=Asia/Phnom_Penh", "-jar", "/app/app.jar"]
