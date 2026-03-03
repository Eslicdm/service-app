FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pricing-service/pom.xml ./pricing-service/pom.xml

RUN mvn -B -f pricing-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline -DskipTests

COPY pricing-service/src ./pricing-service/src
RUN mvn -B -f pricing-service/pom.xml -s /usr/share/maven/ref/settings-docker.xml clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/pricing-service/target/*.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
