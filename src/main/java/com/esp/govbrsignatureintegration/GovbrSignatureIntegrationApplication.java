package com.esp.govbrsignatureintegration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GovbrSignatureIntegrationApplication {

    @Value("${data.servidorOauth}")
    private String servidorOauth;

    @Value("${data.assinaturaApiUri}")
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
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GovbrSignatureIntegrationApplication.class, args);
    }

}
