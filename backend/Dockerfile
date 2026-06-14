# 后端 Spring Boot Dockerfile（多阶段构建，无需手动打包）
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app

# 阿里云 Maven 镜像加速
COPY settings-docker.xml /root/.m2/settings.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
