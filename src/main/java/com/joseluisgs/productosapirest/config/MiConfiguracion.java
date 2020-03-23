package com.joseluisgs.productosapirest.config;

/*
Clase de configuración
 */

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
// Activamos la auditoria, esto por ejemplo nos permite no meter la fecha si no que la tome automáticamente
public class MiConfiguracion {

    // Creamos el bean para el wrapper
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    /**
     * CORS: Configuración más ajustada.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            // Ajustamos una configuración específica para cada serie de métodos
            // Así por cada fuente podemos permitir lo que queremos
            // Por ejemplo ene sta configuración solo permitirmos el dominio producto
            // Permitimos solo un dominio
            // e indicamos los verbos que queremos usar
            // Debes probar con uncliente desde ese puerto
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/producto/**")
                        .allowedOrigins("http://localhost:8888")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .maxAge(3600);
            }

        };
    }
}

