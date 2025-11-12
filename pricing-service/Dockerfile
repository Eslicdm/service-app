FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY pricing-service/pom.xml ./pricing-service/pom.xml

RUN mvn -B -f pricing-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY pricing-service/src ./pricing-service/src
RUN mvn -B -f pricing-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/pricing-service/target/agents/opentelemetry-javaagent.jar /app/agents/opentelemetry-javaagent.jar
COPY --from=build /app/pricing-service/target/*.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
