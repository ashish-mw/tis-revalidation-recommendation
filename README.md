# TIS-HEE-REVALIDATION

Revalidation V2

# Prerequisite

- Java 11
- Maven
- Docker

# To Build

mvn clean install

# To Run

cd application

mvn clean package spring-boot:run

# To execute integration test

mvn clean verify -Pintegration-tests

Above command will setup docker environment on your local machine before it execute integration tests from integration-tests module.