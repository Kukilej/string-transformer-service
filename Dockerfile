# Stage 1: Build the application
FROM openjdk:21-jdk-slim AS build

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy pom.xml and download dependencies (leveraging Docker layer caching)
COPY pom.xml .

# Download Maven dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Package the application into a fat JAR, skipping tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment
FROM openjdk:21-jdk-slim AS runtime

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/string-transformer-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV LOKI_URL=http://loki:3100
ENV SERVER_PORT=8080

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 3: Debugging environment (Optional)
FROM openjdk:21-jdk-slim AS debug

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/string-transformer-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port and debug port
EXPOSE 8080 5005

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=dev
ENV LOKI_URL=http://loki:3100
ENV SERVER_PORT=8080
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

# Define the entry point to run the application with debugging enabled
ENTRYPOINT ["java", "-jar", "app.jar"]
