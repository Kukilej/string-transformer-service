package com.kuzminac.string_transformer_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.api")
@Getter
@Setter
public class OpenApiProperties {

    private String title;
    private String version;
    private String description;
}
