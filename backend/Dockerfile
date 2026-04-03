# ─── Stage 1: Build Spring Boot ───────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# copy pom first — dependency layer gets cached
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Stage 2: Build Whisper ───────────────────────────────────
FROM ubuntu:22.04 AS whisper-builder

RUN apt-get update && apt-get install -y \
    git \
    build-essential \
    cmake \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /whisper
RUN git clone https://github.com/ggerganov/whisper.cpp .
RUN cmake -B build && cmake --build build --config Release

# ─── Stage 3: Run ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# install system dependencies
RUN apt-get update && apt-get install -y \
    ffmpeg \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

# create non-root user
RUN groupadd -r psychologger && \
    useradd -r -g psychologger psychologger

# create required dirs
RUN mkdir -p /app/tmp /app/native-bin && \
    chown -R psychologger:psychologger /app

# copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# copy whisper binary and model
COPY --from=whisper-builder \
    /whisper/build/bin/whisper-cli \
    ./native-bin/whisper-cli
RUN chmod +x ./native-bin/whisper-cli

# copy whisper model (not in git — must exist locally)
COPY native-bin/ggml-small.bin ./native-bin/ggml-small.bin

RUN chown -R psychologger:psychologger /app

USER psychologger

EXPOSE 8080

ENTRYPOINT ["java", \
    "-Dspring.profiles.active=docker", \
    "-jar", "app.jar"]