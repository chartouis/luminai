# Builder stage: Build with OpenJDK 23 and Gradle 8.11.1
FROM openjdk:23-jdk-slim AS builder

# Install necessary tools
RUN apt-get update && \
    apt-get install -y wget unzip && \
    rm -rf /var/lib/apt/lists/*

# Set Gradle version and install it manually
ENV GRADLE_VERSION=8.11.1
ENV GRADLE_HOME=/opt/gradle
ENV PATH=${GRADLE_HOME}/bin:${PATH}

RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
    unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt && \
    rm gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle

WORKDIR /app
COPY . .

# Build your project
RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon --stacktrace

# Runtime stage: Use a minimal OpenJDK 23 image with ffmpeg
FROM openjdk:23-jdk-slim

# Install ffmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
