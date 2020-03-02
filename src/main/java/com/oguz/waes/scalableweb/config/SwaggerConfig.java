package com.oguz.waes.scalableweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger Configuration Class
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private List<VendorExtension> vendorExtensions = new ArrayList<>();

    private static final Contact OGUZHAN_KARACULLU = new Contact(
            "Oğuzhan Karacullu",
            "https://github.com/ogz00",
            "oguzhan.karacullu@gmail.com"
    );

    private ApiInfo API_INFO = new ApiInfo("Waes Scalable Web Diff Api",
            "High Performed Base 64 Json Collector API",
            "0.0.1",
            "urn:tos",
            OGUZHAN_KARACULLU,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            vendorExtensions);

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(API_INFO)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
