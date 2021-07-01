FROM java:8
WORKDIR /home
ADD ./target/guardian-service-0.0.1-SNAPSHOT.jar /home/guardian-service-0.0.1-SNAPSHOT.jar
EXPOSE 8085
CMD ["java","-jar","/home/guardian-service-0.0.1-SNAPSHOT.jar"]