package com.esp.govbrsignatureintegration.utils;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;

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
}
