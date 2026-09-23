# Build stage
FROM maven:3.8.1-openjdk-11 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy jar from builder
COPY --from=builder /build/target/user-service-*.jar user-service.jar

# Create logs directory
RUN mkdir -p logs && chown -R appuser:appuser /app

USER appuser

EXPOSE 8001

HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -cp user-service.jar org.springframework.boot.loader.JarLauncher \
  || exit 1

ENTRYPOINT ["java", "-jar", "user-service.jar"]
