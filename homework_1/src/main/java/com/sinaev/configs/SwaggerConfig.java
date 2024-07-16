package com.sinaev.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class for setting up Swagger.
 * <p>
 * This class configures Swagger for API documentation. It sets up a Docket bean that
 * selects all APIs and paths to be documented.
 * </p>
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Configures and returns a Docket bean for Swagger documentation.
     *
     * @return configured Docket object
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
