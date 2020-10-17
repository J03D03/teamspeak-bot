#
# Build stage
FROM maven:3.6.3-jdk-8-slim AS build
WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn dependency:copy-dependencies package

#
# Package stage
FROM java:8
COPY --from=build /app/target/teamspeak-bot-1.0-jar-with-dependencies.jar /usr/local/lib/teamspeak-bot.jar
ENTRYPOINT ["java","-cp","/usr/local/lib/teamspeak-bot.jar", "LegacyBot"]