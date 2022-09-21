package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
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

    private String code;

    private GetTokenService getTokenService;

    private AssinarPKCS7Service assinarPKCS7Service;

    /**
     * Construtor que revebe:
     *
     * @param code                {@link String} do CODE retornado como parametro depois que o usuário se autenticou
     *                            no gov.br e usou o código de confirmação que foi enviado via SMS.
     * @param getTokenService     {@link GetTokenService} responsável por fazer request REST para obter o token
     * @param assinarPKCS7Service {@link AssinarPKCS7Service} responsável por enviar o hash na request e obter os bytes
     *                            da assinatura.
     */
    public SignatureContainer(String code, GetTokenService getTokenService, AssinarPKCS7Service assinarPKCS7Service) {
        this.code = code;
        this.getTokenService = getTokenService;
        this.assinarPKCS7Service = assinarPKCS7Service;
    }

    @Override
    public byte[] sign(InputStream data) {
        // Nesse ponto, o data é o InputStream do pdf já preparado, isto é
        // com o espaço alocado para colocarmos a assinatura
        try {
            // Pegando o token para poder assinar o documento
            String token = this.getTokenService.getToken(code);

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
