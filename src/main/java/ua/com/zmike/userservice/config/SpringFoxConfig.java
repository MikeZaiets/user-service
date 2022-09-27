package ua.com.zmike.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("user-service-api")
                .description("Simple API to manage users")
                .version("0.9.22.001-BETA")
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("ua.com.zmike.userservice.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .displayRequestDuration(false)
                .showExtensions(true)
                .filter(false)
                .validatorUrl(null)
                .maxDisplayedTags(null)
                .defaultModelsExpandDepth(-1)
                .operationsSorter(OperationsSorter.ALPHA)
                .tagsSorter(TagsSorter.ALPHA)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .docExpansion(DocExpansion.NONE)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .build();
    }
}
