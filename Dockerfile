# 第一阶段：构建应用 (保持JDK 8)
FROM maven:3.8.6-jdk-8 AS build

# 设置工作目录
WORKDIR /build

# 1. 先单独复制pom文件并下载依赖 (利用缓存)
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. 复制源码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行应用 (保持JDK 8)
FROM openjdk:8-jre-alpine

# 设置工作目录
WORKDIR /app

# 3. 创建非root用户运行应用
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 4. 创建播客文件保存目录并设置权限
RUN mkdir -p /app/podcasts && chown -R appuser:appgroup /app/podcasts

# 5. 从构建阶段复制JAR文件 (使用通配符匹配版本)
COPY --from=build --chown=appuser:appgroup /build/target/*.jar app.jar

# 6. 健康检查 (确保应用有/actuator/health端点)
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q UP || exit 1

EXPOSE 8080

# 7. 添加JVM内存限制参数 (根据需求调整)
ENTRYPOINT ["java", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
