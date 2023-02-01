package com.esp.govbrsignatureintegration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;

@Service
public class GetCertificateService {
    @Autowired
    private WebClient webClientAssinatura;

    public byte[] getCertificadoPublico(String token) {
        String authorization = "Bearer " + token;

        Flux<DataBuffer> dataBuffer = webClientAssinatura
                .get()
                .uri("/certificadoPublico")
                .header("Authorization", authorization)
                .retrieve()
                .bodyToFlux(DataBuffer.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        DataBufferUtils.write(dataBuffer, byteArrayOutputStream).share().blockLast();

        return byteArrayOutputStream.toByteArray();
    }
}
