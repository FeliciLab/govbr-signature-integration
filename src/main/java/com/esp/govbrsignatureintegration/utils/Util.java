package com.esp.govbrsignatureintegration.utils;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PdfSignatureAppearance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private static final String DIGEST_ALGORITHM = DigestAlgorithms.SHA256;

    /**
     * Retorna o base64 de um hash SHA-256.
     *
     * @param data {@link InputStream} com a informação para gerar o hash SHA-256.
     * @return {@link String} do hash SHA-256.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String generateHashSHA256(InputStream data) throws GeneralSecurityException, IOException {
        logger.info("generateHashSHA256 | DIGEST ALGORITHM: {}", DIGEST_ALGORITHM);

        BouncyCastleDigest digest = new BouncyCastleDigest();

        byte[] documentHash = DigestAlgorithms.digest(data, digest.getMessageDigest(DIGEST_ALGORITHM));

        logger.info("generateHashSHA256 | final");

        return Base64.getEncoder().encodeToString(documentHash);
    }

    /**
     * Gerar a URL para acessar o gov.br para obter o code.
     *
     * @param servidorOauth servidor Oauth2.0.
     * @param redirectUri   uri de redirecionamento.
     * @param scope         determina se é em uma assinatura normal ou em lote (sgin para único arquivo e signature_session para arquivos em lote).
     * @param clientId      identificador do cliente.
     * @return @{@link String} para a URL do gov.br.
     */
    public static String getUrlGovbr(String servidorOauth, String redirectUri, String scope, String clientId) {
        return String.format("https://%s/authorize?response_type=code&redirect_uri=%s&scope=%s&client_id=%s", servidorOauth, redirectUri, scope, clientId);
    }

    /**
     * Retorna conteúdo da aparencia da assinatura digiral.
     * @param appearance um {@link PdfSignatureAppearance}
     * @return String com texto que fica na aparência da assinatura digital.
     */
    public static String getPdfSignatureAppearanceContent(PdfSignatureAppearance appearance) {
        Optional<String> pdfSignerCreatorOptional = Optional.ofNullable(appearance.getSignatureCreator()).filter(s -> !s.isEmpty());

        String pdfSignerCreator = pdfSignerCreatorOptional.isPresent() ? pdfSignerCreatorOptional.get() : "Fulano de Tal";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dataFormated = simpleDateFormat.format(new Date());

        return String.format("Assinado digitalmente por: %s\n" + "Data: %s\n", pdfSignerCreator, dataFormated);
    }
}
