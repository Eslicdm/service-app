FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY service-app-gateway/pom.xml ./service-app-gateway/pom.xml

RUN mvn -B -f service-app-gateway/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY service-app-gateway/src ./service-app-gateway/src
RUN mvn -B -f service-app-gateway/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/service-app-gateway/target/agents/opentelemetry-javaagent.jar /app/agents/opentelemetry-javaagent.jar
COPY --from=build /app/service-app-gateway/target/*.jar /app/app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
