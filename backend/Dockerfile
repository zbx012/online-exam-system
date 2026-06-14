# 后端 Spring Boot Dockerfile（多阶段构建）
FROM eclipse-temurin:8-jdk-alpine AS build
WORKDIR /app

# 安装 Maven（Alpine 仓库自带）
RUN apk add --no-cache maven

COPY settings-docker.xml .
COPY pom.xml .
RUN mvn -s settings-docker.xml dependency:go-offline -B
COPY src ./src
RUN mvn -s settings-docker.xml package -DskipTests -B

FROM eclipse-temurin:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx256m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
