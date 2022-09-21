package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.utils.Util;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.IExternalSignatureContainer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;

import java.io.*;
import java.security.GeneralSecurityException;

public class SignatureContainer implements IExternalSignatureContainer {

    private String code;

    private GetTokenService getTokenService;

    private AssinarPKCS7Service assinarPKCS7Service;

    public SignatureContainer(String code, GetTokenService getTokenService, AssinarPKCS7Service assinarPKCS7Service) {
        this.code = code;
        this.getTokenService = getTokenService;
        this.assinarPKCS7Service = assinarPKCS7Service;
    }

    @Override
    public byte[] sign(InputStream data) {
        try {
            String token = this.getTokenService.getToken(code);

            // Gerando o hash do documento preparado
            String hashBase64 = Util.generateHashSHA256(data);

            byte[] pkcs7 = this.assinarPKCS7Service.getAssinaturaPKC7(token, hashBase64);

            return pkcs7;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void modifySigningDictionary(PdfDictionary pdfDictionary) {
        pdfDictionary.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        pdfDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
    }
}
