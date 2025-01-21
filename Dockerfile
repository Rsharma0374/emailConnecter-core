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
FROM eclipse-temurin:17
WORKDIR /app

# Install Nginx using apt (Debian) or apk (Alpine)
RUN if command -v apt > /dev/null; then \
      apt update && apt install -y nginx; \
    elif command -v apk > /dev/null; then \
      apk update && apk add nginx; \
    else \
      echo "Unsupported base image"; exit 1; \
    fi
# Copy Java application
COPY --from=build /app/target/*.jar /app/app.jar

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose ports for Nginx and Java application
EXPOSE 80 10002

# Start both Java app and Nginx
CMD (java -jar /app/app.jar &) && nginx -g 'daemon off;'

