package com.esp.govbrsignatureintegration.signature;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.signatures.IExternalSignatureContainer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class SignatureContainer implements IExternalSignatureContainer {

    private String assinatura;

    public SignatureContainer(String assinatura) {
        this.assinatura = assinatura;
    }

    @Override
    public byte[] sign(InputStream data) {
        try {
            InputStream inputStream = new ByteArrayInputStream(this.assinatura.getBytes());

            byte[] targetArray = new byte[inputStream.available()];

            inputStream.read(targetArray);

            return targetArray;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return new byte[0];
    }

    @Override
    public void modifySigningDictionary(PdfDictionary pdfDictionary) {
        pdfDictionary.put(PdfName.Filter, PdfName.Adobe_PPKLite);
        pdfDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_detached);
    }
}
