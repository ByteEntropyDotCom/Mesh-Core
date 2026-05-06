# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Set environment variables for Java 21 performance
# -XX:+UseZGC: Best for sub-millisecond pauses in high-traffic meshes
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:+UnlockExperimentalVMOptions"

COPY --from=build /app/target/mesh-core-1.0.0.jar app.jar

# Expose the mTLS port
EXPOSE 8443

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]