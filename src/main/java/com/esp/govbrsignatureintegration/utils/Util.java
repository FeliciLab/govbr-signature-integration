package com.esp.govbrsignatureintegration.utils;

import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class Util {

    /**
     * TODO: colocar esse método em um local mais apropriado
     * Para gerar um pacote PKCS#7 contendo a assinatura digital de um HASH SHA-256.
     */
    public static String generateHashSHA256(InputStream data) throws GeneralSecurityException, IOException {
        BouncyCastleDigest digest = new BouncyCastleDigest();

        byte[] documentHash = DigestAlgorithms
                .digest(data, digest.getMessageDigest("SHA256"));

        return Base64.getEncoder().encodeToString(documentHash);
    }
}
