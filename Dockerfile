FROM openjdk:11-jre-slim
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS=${JVM_OPTS:-"-XX:+UseG1GC"}
ENTRYPOINT ["java","-jar","app.jar"]