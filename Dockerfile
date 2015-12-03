FROM zalando/openjdk:8u66-b17-1-2

MAINTAINER Dan Persa <dan.persa@gmail.com>

EXPOSE 8080 8080

RUN mkdir -p /opt/remindmetolive/assets

ADD target/scala-2.11/remindmetolive-assembly-0.0.1.jar /opt/remindmetolive/

ADD target/assets /opt/remindmetolive/assets

WORKDIR /opt/remindmetolive

ENTRYPOINT java $(java-dynamic-memory-opts) -Dassets.dir=/opt/remindmetolive/assets -server -jar remindmetolive-assembly-0.0.1.jar
