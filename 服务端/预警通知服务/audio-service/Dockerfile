FROM java:8
WORKDIR /home
ADD ./target/audio-service-0.0.1-SNAPSHOT.jar /home/audio-service-0.0.1-SNAPSHOT.jar
EXPOSE 8086
CMD ["java","-jar","/home/audio-service-0.0.1-SNAPSHOT.jar"]