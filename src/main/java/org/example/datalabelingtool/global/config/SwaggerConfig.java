package org.example.datalabelingtool.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                        .components(new Components())
                        .info(apiInfo());
        }

        private io.swagger.v3.oas.models.info.Info apiInfo() {
                return new Info()
                        .title("Text2SQL Data Labeling & Management API")
                        .description("This API provides endpoints for data labeling, annotation, and management in a Text2SQL system.")
                        .version("1.0.0");
        }
}
