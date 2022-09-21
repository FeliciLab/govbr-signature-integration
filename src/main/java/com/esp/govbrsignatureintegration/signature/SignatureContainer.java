package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.utils.Util;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.IExternalSignatureContainer;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Classe que encapsula o processo de assinatura de um documento pdf com a api do gov.br
 */
public class SignatureContainer implements IExternalSignatureContainer {
    private String token;

    private AssinarPKCS7Service assinarPKCS7Service;

    /**
     * Construtor que revebe:
     *
     * @param token               {@link String} token de autenticação
     * @param assinarPKCS7Service {@link AssinarPKCS7Service} responsável por enviar o hash na request e obter os bytes
     *                            da assinatura.
     */
    public SignatureContainer(String token, AssinarPKCS7Service assinarPKCS7Service) {
        this.token = token;
        this.assinarPKCS7Service = assinarPKCS7Service;
    }

    @Override
    public byte[] sign(InputStream data) {
        try {
            // Gerando o hash do documento preparado
            String hashBase64 = Util.generateHashSHA256(data);

            byte[] pkcs7 = this.assinarPKCS7Service.getAssinaturaPKC7(token, hashBase64);

            // Aqui vai os bytes que serão colocados em hexadecimal no documento
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
