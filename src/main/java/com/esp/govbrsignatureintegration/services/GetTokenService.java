package com.esp.govbrsignatureintegration.services;

import com.esp.govbrsignatureintegration.models.GetTokenReturnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * TODO: escrever documentação aqui
 */
@Service
public class GetTokenService {

    @Value("${data.redirectUri}")
    private String redirectUri;

    @Value("${data.clientId}")
    private String clientId;

    @Value("${data.secret}")
    private String secret;
    @Autowired
    private WebClient webClient;

    public String getToken(String code) {
        Mono<GetTokenReturnModel> tokenReturnModelMono = this.webClient.method(HttpMethod.POST).
                uri(uriBuilder -> uriBuilder.path("/token")
                        .queryParam("code", code)
                        .queryParam("client_id", this.clientId)
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_secret", this.secret)
                        .queryParam("redirect_uri", this.redirectUri).build())
                .retrieve()
                .bodyToMono(GetTokenReturnModel.class);

        System.out.println(tokenReturnModelMono);

        GetTokenReturnModel getTokenReturnModel = tokenReturnModelMono.block();

        return getTokenReturnModel.getAccess_token();
    }
}
