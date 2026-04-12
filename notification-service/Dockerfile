FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml ./
COPY common-security/pom.xml common-security/
COPY notification-service/pom.xml notification-service/
RUN mvn dependency:go-offline -pl notification-service -am -q

COPY common-security/src common-security/src
COPY notification-service/src notification-service/src
RUN mvn package -pl notification-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/notification-service/target/*.jar notification-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
