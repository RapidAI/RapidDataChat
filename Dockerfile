# 使用 Eclipse Temurin OpenJDK 17 作为基础镜像
FROM eclipse-temurin:17-jdk-alpine

# 安装 Nginx
RUN apk update && apk add nginx

# 创建应用程序工作目录
WORKDIR /app

# 复制后端构建好的 JAR 文件到容器中
COPY backend/target/backend-0.0.1.jar /app/app.jar

# 复制前端构建好的静态文件到 Nginx 的默认静态资源目录
COPY frontend/dist /usr/share/nginx/html

# 复制 Nginx 配置文件
COPY base/nginx.conf /etc/nginx/nginx.conf

# 设置环境变量
ENV JAVA_OPTS=""

# 配置 Nginx 启动和 Java 后端应用启动
# 容器启动时，先启动 Nginx，然后启动后端 Java 应用
CMD ["sh", "-c", "nginx && java $JAVA_OPTS -jar /app/app.jar"]

# 暴露 Nginx 监听的端口
EXPOSE 8081 8080
