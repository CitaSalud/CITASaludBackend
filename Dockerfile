# Runtime stage - expects pre-built JAR from CI/CD pipeline
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the pre-built JAR from the CI/CD pipeline
COPY artifacts/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
