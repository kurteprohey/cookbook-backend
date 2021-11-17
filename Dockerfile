FROM openjdk:10-jre-slim
COPY build/libs/*.jar app.jar
CMD java -Xms128m -Xmx128m -Xss512k -jar /app.jar
