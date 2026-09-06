FROM maven:3 AS build
LABEL authors="nipin"
LABEL name="checklist"
WORKDIR /checklist
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-ea-1-jdk-oracle
WORKDIR /application
COPY --from=build /checklist/target/*.jar app.jar
EXPOSE 8080
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]