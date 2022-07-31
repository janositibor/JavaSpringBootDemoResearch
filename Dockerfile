FROM eclipse-temurin:17
WORKDIR /opt/app
COPY target/*.jar research.jar
CMD ["java", "-jar", "research.jar"]