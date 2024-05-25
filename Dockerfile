FROM docker.io/maven:3.9.6-eclipse-temurin-22-alpine as maven-build
COPY src /src
COPY pom.xml /pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package
FROM docker.io/eclipse-temurin:22-jre-alpine
COPY --from=maven-build /target/*jar /waze-toll-tool.jar
EXPOSE 8080
CMD ["java", "-jar", "waze-toll-tool.jar"]