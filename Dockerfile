# 使用 Eclipse Temurin OpenJDK 17 作为基础镜像
FROM eclipse-temurin:17-jdk-alpine

# 创建应用程序工作目录
WORKDIR /app

# 复制后端构建好的 JAR 文件到容器中
COPY backend/target/backend-0.0.1.jar app.jar

# 复制前端构建好的静态文件到后端的静态资源目录
COPY frontend/build /app/public

# 设置环境变量
ENV JAVA_OPTS=""

# 容器启动时执行的命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# 暴露应用端口
EXPOSE 8080
