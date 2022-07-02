FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} fintech-app.jar
ENTRYPOINT ["java", "-jar", "fintech-app.jar"]