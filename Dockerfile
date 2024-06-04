FROM debian:latest AS BUILD

RUN apt-get update && apt-get install -y openjdk-17-jdk

COPY . .

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

ARG TESTS_RBBTMQ_ADDRESSES

RUN ./mvnw clean install -DTESTS_RBBTMQ_ADDRESSES=${TESTS_RBBTMQ_ADDRESSES}

FROM openjdk:17-jdk-slim

COPY --from=build ./target/posts-1.0.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]