# Start with a base image that includes Maven and JDK 21
FROM maven:3.8.8-eclipse-temurin-21-alpine as builder

# Copy the project files into the Docker image
COPY . /app

WORKDIR /app

# Build the application
RUN mvn clean package

# Use the official JDK 21 image for the final image
FROM openjdk:21

# Copy the JAR from the builder stage
COPY --from=builder /app/target/eshop-api-0.0.1-SNAPSHOT.jar Eshop-api.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "/Eshop-api.jar"]
