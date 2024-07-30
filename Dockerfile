# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Give execute permissions to the Maven wrapper (mvnw script)
RUN chmod +x ./mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Use a smaller base image for the final build
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]