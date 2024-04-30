# Build stage
FROM maven:3.8.5-openjdk-17 AS BUILD_STAGE
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Production stage
FROM openjdk:17-jdk-alpine AS PRODUCTION_STAGE
COPY --from=BUILD_STAGE /target/app.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]
