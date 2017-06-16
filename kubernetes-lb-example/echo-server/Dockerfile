FROM openjdk:8
 
COPY target/lib /app/lib
COPY target/echo-server-1.0-SNAPSHOT.jar /app/echo-server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/echo-server.jar"]
 

