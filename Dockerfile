# 第一阶段：构建应用
FROM maven:3.8.6-jdk-8 AS build

# 设置工作目录
WORKDIR /build

# 复制 pom.xml 和所有源代码
COPY pom.xml .
COPY src ./src

# 执行 Maven 构建
RUN mvn clean package

# 第二阶段：运行应用
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 创建播客文件保存目录
RUN mkdir -p /app/podcasts

# 从构建阶段复制 JAR 文件
COPY --from=build /build/target/FunnyUtilsApplication-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]


# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
