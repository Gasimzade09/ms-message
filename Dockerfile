# Stage 1: сборка
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

# Stage 2: runtime
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/build/libs/ms-message.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ms-message.jar"]
