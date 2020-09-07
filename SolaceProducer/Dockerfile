FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ADD target/SolaceProducer-0.0.1-SNAPSHOT.jar SolaceProducer-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/SolaceProducer-0.0.1-SNAPSHOT.jar"]
