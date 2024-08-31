FROM openjdk:11-jre-slim
#RUN /usr/bin/mvn clean package
COPY target/ShareCloud-backend-1.0-SNAPSHOT.jar /data/ShareCloud-backend-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/data/ShareCloud-backend-1.0-SNAPSHOT.jar"]
