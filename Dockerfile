
FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /com


COPY pom.xml .


RUN mvn dependency:go-offline


COPY src ./src

RUN mvn clean compile -X


RUN mvn clean package -DskipTests -Dmaven.compiler.failOnError=false

FROM amazoncorretto:21-alpine
WORKDIR /com
COPY --from=build /com/target/*.jar com.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]