package com.esp.govbrsignatureintegration;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RequestsApiGovBr {
    /**
     * Para gerar um pacote PKCS#7 contendo a assinatura digital de um HASH SHA-256
     * utilizando a chave privada do usuário, deve-se fazer uma requisição HTTP POST
     * para o endereço https://assinatura-api.staging.iti.br/externo/v2/assinarPKCS7
     */
    public static CloseableHttpResponse assinarPKCS7(CloseableHttpClient httpclient, InputStream data, String token) throws IOException, GeneralSecurityException {
        HttpPost post = new HttpPost("https://assinatura-api.staging.iti.br/externo/v2/assinarPKCS7");
        post.setEntity(new StringEntity("{\"hashBase64\": \"" + hashSHA256(data) + "\"}", ContentType.APPLICATION_JSON));
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", token);

        CloseableHttpResponse response = httpclient.execute(post);

        HttpEntity entity = response.getEntity();

        return response;
    }

    /**
     * Para gerar um pacote PKCS#7 contendo a assinatura digital de um HASH SHA-256.
     */
    private static String hashSHA256(InputStream data) throws GeneralSecurityException, IOException {
        String hashAlgorithm = "SHA256";
        BouncyCastleDigest digest = new BouncyCastleDigest();
        byte[] documentHash = DigestAlgorithms.digest(data, digest.getMessageDigest(hashAlgorithm));
        return Base64.getEncoder().encodeToString(documentHash);
    }
}
