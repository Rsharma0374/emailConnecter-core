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
#

# Step 1: Build the Java Maven application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Setup NGINX and Java Runtime
FROM openjdk:17-alpine
WORKDIR /app

# Install Nginx
RUN apk update && apk add nginx && mkdir -p /run/nginx

# Copy Java application
COPY --from=build /app/target/*.jar /app/app.jar

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose ports for Nginx and Java application
EXPOSE 80 10002

# Start both Java app and Nginx
CMD (java -jar /app/app.jar &) && nginx -g 'daemon off;'

