package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class SignatureManager {
    private String token;

    private AssinarPKCS7Service assinarPKCS7Service;

    public SignatureManager(String token, AssinarPKCS7Service assinarPKCS7Service) {
        this.token = token;
        this.assinarPKCS7Service = assinarPKCS7Service;
    }

    // Usado para gerar os bytes de um arquivo assinado
    public byte[] getBytesPdfSigned(InputStream pdfInputStream) throws IOException, GeneralSecurityException {
        PdfReader pdfReader = new PdfReader(pdfInputStream);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfSigner pdfSigner = new PdfSigner(pdfReader, byteArrayOutputStream, new StampingProperties());

        Rectangle rectangle = new Rectangle(320, 150, 100, 50);

        PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();

        // appearance.setImage(this.govbrImageData);

        // Mudando as Captions
        appearance.setReasonCaption("Razão: ");
        appearance.setLocationCaption("Localização: ");

        SignatureContainer signatureContainer = new SignatureContainer(this.token, this.assinarPKCS7Service);

        appearance.setReason("SIGN.GOV.BR").setLocation("ESP").setPageRect(rectangle).setPageNumber(1);

        pdfSigner.setFieldName("Sig");

        pdfSigner.signExternalContainer(signatureContainer, 8192);

        byte[] outputBytes = byteArrayOutputStream.toByteArray();

        return outputBytes;
    }
}
