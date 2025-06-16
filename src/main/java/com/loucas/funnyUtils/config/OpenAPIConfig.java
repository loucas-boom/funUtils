package com.loucas.funnyUtils.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OpenAPIConfig  {
    @Bean
    public OpenAPI springShopOpenAPI() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("author", "Loucas");
            return new OpenAPI()
                    .info(new Info().title("Funny Utils API")
                            .description("Funny Utils API 文档")
                            .version("v1.0.0")
                            .extensions(map)
                            .contact(new Contact()
                                    .name("Loucas")
                                    .email("glh0759@gmail.com"))
                            .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
        } catch (Exception e) {
            // 记录异常信息到日志
            System.err.println("OpenAPI 配置异常: " + e.getMessage());
            // 抛出运行时异常以便 Spring 启动失败时明确提示配置问题
            throw new RuntimeException("OpenAPI 配置初始化失败", e);
        }
    }
}
