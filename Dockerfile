FROM openjdk:11-jre-slim
#RUN /usr/bin/mvn clean package
COPY target/Sharemount-backend-1.0-SNAPSHOT.jar /data/Sharemount-backend-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/data/Sharemount-backend-1.0-SNAPSHOT.jar"]
