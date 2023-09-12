# Use an official OpenJDK runtime as a parent image
FROM adoptopenjdk:17-jre-hotspot
# Set the working directory in the container
WORKDIR /app
# Copy the JAR file into the container at /app
COPY target/your-app.jar /app/your-app.jar
# Run the Spring Boot application
CMD ["java", "-jar", "your-app.jar"]
