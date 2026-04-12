FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml ./
COPY common-security/pom.xml common-security/
COPY payment-service/pom.xml payment-service/
RUN mvn dependency:go-offline -pl payment-service -am -q
COPY common-security/src common-security/src
COPY payment-service/src payment-service/src
RUN mvn package -pl payment-service -am -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/payment-service/target/*.jar payment-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
