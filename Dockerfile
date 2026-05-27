FROM maven:3.9.16-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21 AS run
COPY --from=build app/target/AstonIntensiveHomework-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar", "app.jar"]