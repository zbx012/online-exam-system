# 后端 Spring Boot Dockerfile
FROM openjdk:8-jre-alpine

WORKDIR /app

# 复制 jar 包
COPY target/*.jar app.jar

# 设置 JVM 内存限制（适配 2GB 小机器）
ENV JAVA_OPTS="-Xms128m -Xmx256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
