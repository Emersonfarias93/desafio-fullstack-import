package br.com.esdevcode.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiDocumentationConfig {

    @Bean
    public OpenAPI desafioFullstackImportOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Fullstack Import API")
                        .version("v1")
                        .description("API para importacao de leads via CSV, auditoria de lotes, dashboard operacional e acompanhamento em tempo real.")
                        .contact(new Contact()
                                .name("Equipe Desafio Fullstack Import"))
                        .license(new License()
                                .name("Uso interno para avaliacao tecnica")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Ambiente atual")
                ));
    }
}
