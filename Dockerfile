FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

ENV TZ=Asia/Phnom_Penh
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar
USER spring

ARG APP_PORT=3333
ENV SERVER_PORT=${APP_PORT}
EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Duser.timezone=Asia/Phnom_Penh", "-jar", "/app/app.jar"]
