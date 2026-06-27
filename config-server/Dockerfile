FROM maven:3.9.16-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21 AS run
ARG PORT
COPY --from=build app/target/config-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "app.jar"]