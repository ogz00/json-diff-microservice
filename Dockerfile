### BUILD image

FROM maven:3-jdk-11 as builder

# create app folder for sources

RUN mkdir -p /build

WORKDIR /build

COPY pom.xml /build

#Download all required dependencies into one layer

RUN mvn -B dependency:resolve dependency:resolve-plugins

#Copy source code

COPY src /build/src

# Build application

RUN mvn package  -DskipTests

#### Stage 2: A minimal docker image with command to run the app 
FROM openjdk:11-slim as runtime

#Set app home folder

ENV APP_HOME /app

ENV JAVA_OPTS=""

#Create base app folder

RUN mkdir $APP_HOME

#Create folder to save configuration files

RUN mkdir $APP_HOME/config

#Create folder with application logs

RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log

VOLUME $APP_HOME/config

WORKDIR $APP_HOME

#Copy executable jar file from the builder image

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]

# -Dspring.config.location=/app/resources/application.properties

#Second option using shell form:

#ENTRYPOINT exec java $JAVA_OPTS -jar app.jar $0 $@