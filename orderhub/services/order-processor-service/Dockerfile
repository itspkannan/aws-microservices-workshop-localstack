FROM azul/zulu-openjdk:17.50.19-ca-jdk

ARG APP_USER=appuser
ARG SERVICE_NAME=app

RUN groupadd --system appgroup && \
    useradd --system --create-home --gid appgroup appuser

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
