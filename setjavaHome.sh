#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
cd ~/Downloads/spark-1.3.0
mvn -DskipTests clean package
