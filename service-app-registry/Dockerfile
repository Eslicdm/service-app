FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY service-app-registry/pom.xml ./service-app-registry/pom.xml

RUN mvn -B -f service-app-registry/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY service-app-registry/src ./service-app-registry/src
RUN mvn -B -f service-app-registry/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/service-app-registry/target/*.jar /app/app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
