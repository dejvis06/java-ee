#!/bin/sh
mvn clean install && docker build -t com.example/java-ee .
docker run -p 8080:8080 -p 4848:4848 --name java-ee com.example/java-ee
