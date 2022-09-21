package com.esp.govbrsignatureintegration.configs;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
public class GovbrConfigs {

    @Bean
    public ImageData govbrImageData() throws MalformedURLException {
        ImageData imageData = ImageDataFactory.create("./assets/gov-br-logo.png");
        return imageData;
    }
}
