FROM amazoncorretto:21-alpine AS builder
LABEL authors="dcloud99"
WORKDIR /workspace/app
COPY gradlew .
RUN chmod +x gradlew
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew bootJar -x test --no-daemon
RUN java -Djarmode=tools -jar build/libs/app.jar extract --layers --destination extracted

FROM amazoncorretto:21-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring /workspace/app/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /workspace/app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /workspace/app/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /workspace/app/extracted/application/ ./

USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]