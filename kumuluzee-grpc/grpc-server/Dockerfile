FROM isuper/java-oracle

COPY target /usr/src/myapp

WORKDIR /usr/src/myapp

EXPOSE 8443

CMD ["java", "-jar", "grpc-server-1.0-SNAPSHOT.jar"]