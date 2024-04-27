# Build stage
FROM maven:3.8.5-openjdk-17 AS BUILD_STAGE
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Production stage
FROM openjdk:17-jdk-slim AS PRODUCTION_STAGE
COPY --from=BUILD_STAGE /app/target/app.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
