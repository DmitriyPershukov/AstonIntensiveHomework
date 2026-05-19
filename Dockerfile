FROM maven:3.9.16-eclipse-temurin-21
WORKDIR /app
COPY . /app
RUN mvn clean package
ENTRYPOINT ["java","-jar", "target/AstonIntensiveHomework-1.0-SNAPSHOT.jar"]