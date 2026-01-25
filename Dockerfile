# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app/backend

COPY backend/pom.xml ./pom.xml
COPY backend/src ./src

RUN mvn -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/backend/target/*.jar ./app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
