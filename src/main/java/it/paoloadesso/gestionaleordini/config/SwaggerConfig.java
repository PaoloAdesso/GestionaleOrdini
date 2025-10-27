package it.paoloadesso.gestionaleordini.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gestionale Ordini - Sistema Operativo",
                description = "API per camerieri e cucina: ordini, stati, consultazione",
                version = "1.0.0"
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = """
                SISTEMA OPERATIVO - Personale di sala e cucina:
                
                CAMERIERI:
                • mario/cameriere
                • giulia/cameriere
                • sara/cameriere
                
                CUOCHI:
                • chef/cuoco
                • luca/cuoco
                • antonio/cuoco
                
                ADMIN OPERATIVO:
                • admin/admin (accesso completo)
                
                Sistema per tablet e PC in sala/cucina
                """
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Gestionale Ordini - Sistema Operativo")
                        .description("API operative per la gestione di ordini, tavoli e prodotti")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Paolo Adesso")
                                .email("paoloadesso@outlook.it")));
    }
}
