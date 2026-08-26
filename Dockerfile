FROM eclipse-temurin:21-jre
COPY api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
