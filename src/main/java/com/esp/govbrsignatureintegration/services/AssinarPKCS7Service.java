package com.esp.govbrsignatureintegration.services;

import com.esp.govbrsignatureintegration.models.AssinarPKCS7RequestModel;
import com.esp.govbrsignatureintegration.models.GetTokenReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.image.DataBuffer;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class AssinarPKCS7Service {
    @Autowired
    private WebClient webClientAssinatura;

    /**
     * TODO: Estou com problema aqui
     *
     * @param token
     * @param hashBase64
     * @return
     */
    public byte[] getAssinaturaPKC7(String token, String hashBase64) {
        AssinarPKCS7RequestModel assinarPKCS7RequestModel = new AssinarPKCS7RequestModel(hashBase64);

        Mono<byte[]> mono = this.webClientAssinatura.post()
                .uri("/assinarPKCS7")
                .headers(headers -> headers.setBearerAuth(token))
                .body(assinarPKCS7RequestModel, AssinarPKCS7RequestModel.class)
                .retrieve()
                .bodyToMono(byte[].class);

        byte[] pkc7 = mono.block();

        return pkc7;
    }

}
