FROM openjdk:8
 
COPY target/lib /app/lib
COPY target/echo-client-simple-1.0-SNAPSHOT.jar /app/echo-client-simple.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/echo-client-simple.jar"]
 

