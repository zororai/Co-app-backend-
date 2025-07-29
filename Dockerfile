FROM maven:3.9.10-sapmachine-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Final runtime image
FROM openjdk:17-slim
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]





# FROM openjdk:17



# ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
# RUN chmod +x /wait

# WORKDIR /app

# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./
# RUN chmod +x mvnw
# RUN ./mvnw dependency:go-offline

# COPY src ./src

# CMD ["./mvnw", "spring-boot:run"]