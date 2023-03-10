FROM maven:3.8.6-openjdk-11-slim as builder
WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11.0.18_10-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/REST-API-0.0.1-SNAPSHOT.jar .
EXPOSE 3000
CMD ["java", "-jar", "REST-API-0.0.1-SNAPSHOT.jar"]