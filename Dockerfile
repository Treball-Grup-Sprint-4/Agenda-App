
FROM maven:4.0.0-rc-5-amazoncorretto-25-debian-trixie

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY src/ ./src/

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/agenda-app-1.0-SNAPSHOT.jar"]
