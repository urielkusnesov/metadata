# Build project
FROM maven:3.6.3-jdk-14 as builder
WORKDIR /school
COPY . .
#REMOVE SKIPTEST WHEN JUNIT
RUN mvn clean install

# Run project
FROM openjdk:14-jdk-alpine
VOLUME /tmp
COPY --from=builder /school/target/*.jar app.jar
COPY dev/docker.properties config/docker.properties

RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENV CONSUL_URL=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS  -Djava.security.egd=file:/dev/./urandom -jar /app.jar --spring.config.location=file:/config/docker.properties "]
