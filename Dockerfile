## Use a base image with JDK
#FROM openjdk:17-jdk-slim
#
## Set the working directory
#WORKDIR /emailConnecter-core
#
## Copy the JAR file into the container
#COPY target/*.jar emailConnector.jar
#
## Create the logs directory inside the container
#RUN mkdir -p /opt/logs && chmod 755 /opt/logs
#
## Expose the port your app will run on
#EXPOSE 10002
#
## Run the JAR file
#ENTRYPOINT ["java", "-jar", "emailConnector.jar"]






# Use an official Maven image to build the application
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Set the working directory for the build
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml ./
COPY src ./src

# Run the Maven build with the production profile
RUN mvn clean install -P prod

# Use an official OpenJDK runtime as the base image for the final build
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /emailConnecter-core

# Copy the JAR file from the Maven build stage
COPY --from=build /app/target/*.jar emailConnector.jar

# Create the logs directory inside the container
RUN mkdir -p /opt/logs && chmod 755 /opt/logs

# Expose the port your app will run on
EXPOSE 10002

# Define the command to run the app
ENTRYPOINT ["java", "-jar", "emailConnector.jar"]

