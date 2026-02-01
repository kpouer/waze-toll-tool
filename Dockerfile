FROM docker.io/maven:3.9-eclipse-temurin-25-alpine as maven-build
COPY src /src
COPY pom.xml /pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package
FROM docker.io/eclipse-temurin:25-jre-alpine
RUN adduser -D waze-toll-tool
COPY --from=maven-build /target/*jar /waze-toll-tool.jar
USER waze-toll-tool
EXPOSE 8080
CMD ["java", "-jar", "waze-toll-tool.jar"]