package org.example.zhao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("辅导机构管理系统 API")
                        .version("v1")
                        .description("SpringBoot 2.7.x + MyBatis-Plus + JWT"));
    }
}

