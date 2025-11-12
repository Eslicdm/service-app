FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY member-service/pom.xml ./member-service/pom.xml

RUN mvn -B -f member-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY member-service/src ./member-service/src
RUN mvn -B -f member-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/member-service/target/agents/opentelemetry-javaagent.jar /app/agents/opentelemetry-javaagent.jar
COPY --from=build /app/member-service/target/*.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
