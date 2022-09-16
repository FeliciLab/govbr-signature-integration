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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class AssinarPKCS7Service {
    @Autowired
    private WebClient webClientAssinatura;

    /**
     * TODO: documentar
     * @param token
     * @param hashBase64
     * @return
     */
    public String getAssinaturaPKC7(String token, String hashBase64) {
        String authorization = "Bearer " + token;

        AssinarPKCS7RequestModel assinarPKCS7RequestModel = new AssinarPKCS7RequestModel(hashBase64);

        Mono<String> mono = this.webClientAssinatura.method(HttpMethod.POST)
                .uri("/assinarPKCS7")
                .header("Authorization", authorization)
                .body(Mono.just(assinarPKCS7RequestModel), AssinarPKCS7RequestModel.class)
                .retrieve()
                .bodyToMono(String.class);

        String pkcs7 = mono.block();

        return pkcs7;
    }

}
