package com.esp.govbrsignatureintegration;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignatureContainer;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class SignatureContainer implements IExternalSignatureContainer {
    @Override
    public byte[] sign(InputStream data) {
        try {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = RequestsApiGovBr.assinarPKCS7(httpclient, data, "")) {

                    HttpEntity entity = response.getEntity();

                    InputStream inputStream = entity.getContent();
                    byte[] targetArray = new byte[inputStream.available()];
                    inputStream.read(targetArray);

                    return targetArray;
                }
            }
        } catch (IOException ioe) {
//            LOGGER.log(Level.SEVERE, "IOEXCEPTION", ioe);
        } catch (GeneralSecurityException gse) {
//            LOGGER.log(Level.SEVERE, "GENERALSECURITYEXCEPTION", gse);
        }

        return new byte[0];
    }

    @Override
    public void modifySigningDictionary(PdfDictionary pdfDictionary) {
        pdfDictionary.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        pdfDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
    }

    /**
     * Usado para gerar o token que será usado
     */
    private static CloseableHttpResponse geToken(String code)
            throws IOException, GeneralSecurityException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://cas.staging.iti.br/oauth2.0/token");
        return httpclient.execute(post);
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
