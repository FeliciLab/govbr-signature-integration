package com.esp.govbrsignatureintegration.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuração dos WebClientes.
 */
@Configuration
public class WebClientsConfig {
    @Value("${govbr.servidorOauth}")
    private String servidorOauth;

    @Value("${govbr.assinaturaApiUri}")
    private String assinaturaApiUri;

    @Bean
    public WebClient webClientOauth(WebClient.Builder builder) {
        String baseUrl = "https://" + servidorOauth;

        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient webClientAssinatura(WebClient.Builder builder) {
        return builder
                .baseUrl(this.assinaturaApiUri)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
