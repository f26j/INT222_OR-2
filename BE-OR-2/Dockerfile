# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /backend
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Production stage
FROM openjdk:17-jdk-alpine AS production
COPY --from=build /backend/target/app.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
