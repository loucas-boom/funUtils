# 使用包含 JDK 和 Maven 的基础镜像
FROM maven:3.8.6-jdk-8

# 设置工作目录
WORKDIR /app

# 创建播客文件保存目录
RUN mkdir -p /app/podcasts

# 复制项目源码和 pom.xml
COPY pom.xml .
COPY src ./src

# 执行 Maven 构建
RUN mvn clean package

# 获取生成的 JAR 文件名（自动匹配 .jar 文件）
RUN APP_JAR=$(ls target/*.jar | head -n 1) && \
    cp $APP_JAR app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
