FROM openjdk:8
 
COPY target/lib /app/lib
COPY target/echo-client-lb-api-1.0-SNAPSHOT.jar /app/echo-client-lb-api.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/echo-client-lb-api.jar"]
 

