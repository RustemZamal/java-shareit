FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8082
COPY target/shareit-server-0.0.1-SNAPSHOT.jar server.jar
ENTRYPOINT ["java","-jar", "/server.jar"]

