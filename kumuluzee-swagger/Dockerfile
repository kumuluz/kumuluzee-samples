FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/classes /app/classes
ADD ./target/dependency /app/dependency

EXPOSE 8080

CMD ["java", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
