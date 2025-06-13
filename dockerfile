FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/myapp-k8s-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

