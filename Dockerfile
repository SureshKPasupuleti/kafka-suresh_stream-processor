FROM openjdk:8
ADD build/libs/com.kafka.stream.processor-0.1.0.jar docker-stream-kafka-processor.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","docker-stream-kafka-processor.jar"]