FROM debian:latest AS BUILD

RUN apt-get update && apt-get install -y openjdk-17-jdk

COPY . .

RUN chmod +x mvnw && ./mvnw clean install

FROM openjdk:17-jdk-slim

COPY --from=build ./target/posts-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]