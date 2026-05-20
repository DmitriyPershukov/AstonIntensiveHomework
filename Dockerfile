FROM maven:3.9.16-eclipse-temurin-21
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -o
ENTRYPOINT ["java","-jar", "target/AstonIntensiveHomework-1.0-SNAPSHOT.jar"]