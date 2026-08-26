package com.jms.assignment1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "학습 플랫폼 API",
                version = "1.0",
                description = "단원별 문제 풀이 및 풀이 이력 조회 API"
        )
)
@Configuration
public class SwaggerConfig {
}
