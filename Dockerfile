FROM openjdk:11-jre-slim
COPY application/target/revalidation-uber.jar app.jar
ENV JAVA_OPTS=${JVM_OPTS:-"-XX:+UseG1GC"}
ENTRYPOINT ["java","-jar","app.jar"]