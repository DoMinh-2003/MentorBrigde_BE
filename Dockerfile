FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/be.jar
COPY ${JAR_FILE} be.jar
ENTRYPOINT ["java","-jar","/be.jar"]