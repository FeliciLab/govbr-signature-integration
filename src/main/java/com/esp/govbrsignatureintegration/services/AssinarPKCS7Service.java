package com.esp.govbrsignatureintegration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;

/**
 * Service para retornar a assinatura
 * Vide: https://manual-integracao-assinatura-eletronica.readthedocs.io/en/latest/iniciarintegracao.html#api-de-assinatura-digital-gov-br
 */
@Service
public class AssinarPKCS7Service {
    @Autowired
    private WebClient webClientAssinatura;

    public byte[] getAssinaturaPKC7(String token, String hashBase64) {
        String authorization = "Bearer " + token;

        try {
            Flux<DataBuffer> dataBuffer = webClientAssinatura
                    .post()
                    .uri("/assinarPKCS7")
                    .header("Authorization", authorization)
                    .bodyValue("{\"hashBase64\": \"" + hashBase64 + "\"}")
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            DataBufferUtils.write(dataBuffer, byteArrayOutputStream).share().blockLast();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            System.out.println("getAssinaturaPKC7 Exception:");
            System.err.println(exception.getMessage());
        }

        return new byte[0];
    }

}
