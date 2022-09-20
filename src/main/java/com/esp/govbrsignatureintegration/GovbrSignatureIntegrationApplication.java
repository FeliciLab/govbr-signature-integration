package com.esp.govbrsignatureintegration;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;

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
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024))
                        .build())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public ImageData govbrImageData() throws MalformedURLException {
        ImageData imageData = ImageDataFactory.create("./assets/gov-br-logo.png");
        return imageData;
    }

    public static void main(String[] args) {
        SpringApplication.run(GovbrSignatureIntegrationApplication.class, args);
    }

}
