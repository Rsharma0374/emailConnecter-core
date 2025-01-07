# Use a base image with JDK
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /emailConnecter-core

# Copy the JAR file into the container
COPY target/*.jar emailConnector.jar

# Create the logs directory inside the container
RUN mkdir -p /opt/logs && chmod 755 /opt/logs

# Expose the port your app will run on
EXPOSE 10002

# Run the JAR file
ENTRYPOINT ["java", "-jar", "emailConnector.jar"]
