# 第一阶段：构建应用
FROM maven:3.8.6-jdk-8 AS build

# 设置工作目录
WORKDIR /build

# 确保当前目录下有 pom.xml 和 src 目录
COPY pom.xml .
COPY src ./src

# 构建前列出当前目录内容用于调试（可选）
RUN ls -la

# 执行 Maven 构建
RUN mvn clean package

# 构建后检查 target 是否生成
RUN ls -l /build/target/


# 第二阶段：运行应用
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 创建播客文件保存目录
RUN mkdir -p /app/podcasts

RUN ls -l /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/FunnyUtilsApplication-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
