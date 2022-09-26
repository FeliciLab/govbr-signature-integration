package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class SignatureManager {
    private static final Logger logger = LoggerFactory.getLogger(SignatureManager.class);

    private static final int ESTIMATED_SIZE = 8192; // Tamanho estimado da assinatura.
    private String token; // token para assinar o documento
    private AssinarPKCS7Service assinarPKCS7Service; // webcliente para fazer a request

    public SignatureManager(String token, AssinarPKCS7Service assinarPKCS7Service) {
        this.token = token;
        this.assinarPKCS7Service = assinarPKCS7Service;
    }

    // Usado para gerar os bytes de um arquivo assinado
    public byte[] getBytesPdfSigned(InputStream pdfInputStream) throws IOException, GeneralSecurityException {
        logger.info("getBytesPdfSigned | init");

        PdfReader pdfReader = new PdfReader(pdfInputStream);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfSigner pdfSigner = new PdfSigner(pdfReader, byteArrayOutputStream, new StampingProperties());

        SignatureContainer signatureContainer = new SignatureContainer(this.token, this.assinarPKCS7Service);

        this.buildAppearence(pdfSigner.getSignatureAppearance());

        pdfSigner.setFieldName("Sig");

        pdfSigner.signExternalContainer(signatureContainer, ESTIMATED_SIZE);

        byte[] outputBytes = byteArrayOutputStream.toByteArray();

        logger.info("getBytesPdfSigned | final");

        return outputBytes;
    }

    private void buildAppearence(PdfSignatureAppearance appearance) {
        // Mudando as Captions
        appearance.setReasonCaption("Razão: ");
        appearance.setLocationCaption("Localização: ");

        Rectangle rectangle = new Rectangle(350, 150, 150, 50);

        appearance.setReason("SIGN.GOV.BR").setLocation("ESP").setPageRect(rectangle).setPageNumber(1);
    }
}
