FROM openjdk:17-jdk
EXPOSE 8082
ADD target/tp-foyer-5.0.0.jar tp-foyer-5.0.0.jar
ENTRYPOINT ["java","-jar","/tp-foyer-5.0.0.jar"]
