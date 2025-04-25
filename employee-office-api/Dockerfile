FROM openjdk:21-slim

WORKDIR /app

ARG JAR_FILE=target/employee-office-api-*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]