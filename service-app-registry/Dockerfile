FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY service-app-registry/pom.xml ./service-app-registry/pom.xml

RUN mvn -B -f service-app-registry/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY service-app-registry/src ./service-app-registry/src
RUN mvn -B -f service-app-registry/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/service-app-registry/target/agents/opentelemetry-javaagent.jar /app/agents/opentelemetry-javaagent.jar
COPY --from=build /app/service-app-registry/target/*.jar /app/app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
