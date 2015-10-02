FROM java:8

MAINTAINER Dan Persa <dan.persa@gmail.com>

EXPOSE 8080 8080

RUN mkdir -p /opt/remindmetolive

ADD target/scala-2.11/remindmetolive-assembly-0.0.1.jar /opt/remindmetolive/

WORKDIR /opt/remindmetolive

ENTRYPOINT java $(java-dynamic-memory-opts) -server -jar remindmetolive-assembly-0.0.1.jar
