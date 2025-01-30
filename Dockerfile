# Use an official Java 21 runtime image
FROM eclipse-temurin:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file to the container
COPY build/libs/scriptorium-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
