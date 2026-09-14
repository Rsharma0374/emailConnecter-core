
# Step 1: Build the Java Maven application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -Pprod dependency:go-offline
COPY src ./src

RUN mvn -B -Pprod package -DskipTests

FROM eclipse-temurin:21-jre-jammy

RUN groupadd --system app && useradd --system --gid app --home-dir /app app \
    && mkdir -p /logs \
    && chown app:app /logs
WORKDIR /app
COPY --from=build /build/target/emailConnecter-core-1.0.0.jar /app/app.jar

EXPOSE 10002

USER app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]