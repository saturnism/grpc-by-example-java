FROM openjdk:8
 
COPY target/lib /app/lib
COPY target/echo-client-lb-dns-1.0-SNAPSHOT.jar /app/echo-client-lb-dns.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/echo-client-lb-dns.jar"]
 

