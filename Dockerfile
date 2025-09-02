FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . /app

RUN apt-get update && apt-get install -y maven && \
    mvn clean install -DskipTests

EXPOSE 8081

CMD ["java", "-jar", "target/CustomWorld-0.0.1-SNAPSHOT.jar"]