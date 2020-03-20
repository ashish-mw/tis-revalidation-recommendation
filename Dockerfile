FROM maven:3.6.3-jdk-11-slim AS build
RUN mkdir /appbuild
COPY . /appbuild/
RUN mvn -f /appbuild/pom.xml clean install

FROM openjdk:11-jre-slim
COPY --from=build /appbuild/application/target/*-uber.jar /app.jar
ENV JAVA_OPTS=${JVM_OPTS:-"-XX:+UseG1GC"}
ENTRYPOINT ["java","-jar","app.jar"]