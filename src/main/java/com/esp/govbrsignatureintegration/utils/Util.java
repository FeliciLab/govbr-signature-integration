package com.esp.govbrsignatureintegration.utils;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class Util {

    /**
     * Retorna o base64 de um hash SHA-256.
     *
     * @param data {@link InputStream} com a informação para gerar o hash SHA-256.
     * @return {@link String} do hash SHA-256.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String generateHashSHA256(InputStream data) throws GeneralSecurityException, IOException {
        BouncyCastleDigest digest = new BouncyCastleDigest();

        byte[] documentHash = DigestAlgorithms.digest(data, digest.getMessageDigest("SHA256"));

        return Base64.getEncoder().encodeToString(documentHash);
    }
}
