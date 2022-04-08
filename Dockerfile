FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app/pd-library
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ["./mvnw", "package"]
FROM openjdk:17-jdk-alpine
ARG PASSWORD
ARG ACTIVE_PROFILE
ARG GOOGLE_APPLICATIONS_CREDENTIALS_JSON
ENV SPRING_PROFILES_ACTIVE=$ACTIVE_PROFILE
ENV SPRING_DATASOURCE_PASSWORD=$PASSWORD
WORKDIR /app
COPY entrypoint.sh /entrypoint.sh
COPY --from=builder /app/pd-library/target/rbc-library-0.0.1-SNAPSHOT.jar rbc-library-0.0.1-SNAPSHOT.jar
EXPOSE 8080
RUN echo $GOOGLE_APPLICATIONS_CREDENTIALS_JSON > ./credentials.json
ENV GOOGLE_APPLICATION_CREDENTIALS=./credentials.json
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]