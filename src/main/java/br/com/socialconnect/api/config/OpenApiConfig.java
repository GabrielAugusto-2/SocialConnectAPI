package br.com.socialconnect.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SocialConnect API")
                        .version("v1.0")
                        .description("API RESTful para gestão de beneficiários e assistência social")
                        .contact(new Contact()
                                .name("SocialConnect")
                                .email("suporte@socialconnect.org.br")));
    }
}
