FROM adoptopenjdk/openjdk11:ubi

WORKDIR /app

RUN mkdir ./img/

COPY ./build/libs/catalizator.jar .

EXPOSE 8080

CMD ["java", "-jar", "catalizator.jar"]
