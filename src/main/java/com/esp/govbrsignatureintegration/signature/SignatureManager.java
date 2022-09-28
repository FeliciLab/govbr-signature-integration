package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
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
import java.net.MalformedURLException;
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

        // Pegando as pegar as dimensões de uma página A4 rotacionada
        Rectangle pageDimensions = PageSize.A4.rotate();

        this.buildAppearence(pdfSigner.getSignatureAppearance(), pageDimensions);

        pdfSigner.setFieldName("Sig");

        pdfSigner.signExternalContainer(signatureContainer, ESTIMATED_SIZE);

        byte[] outputBytes = byteArrayOutputStream.toByteArray();

        logger.info("getBytesPdfSigned | final");

        return outputBytes;
    }

    private void buildAppearence(PdfSignatureAppearance appearance, Rectangle pageDimensions) throws MalformedURLException {
        logger.info("buildAppearence | init");

        float pageWidth = pageDimensions.getWidth();

        float rectangleWidth = 250f;
        float rectangleHeigth = 60f;

        float rectangleX = (pageWidth - rectangleWidth) / 2;
        float rectangleY = rectangleHeigth + 90f;

        Rectangle rectangle = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeigth);

        appearance
                .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION) // Assinatura gráfica e com descrição
                .setReasonCaption("Razão: ") //
                .setLocationCaption("Localização: ") //
                .setContact("Contato") //
                .setSignatureCreator("Dr. Marcelo") //
                .setReason("ESP - Escola de saúde pública do Ceará") //
                .setLocation("Fortaleza - CE") //
                .setSignatureGraphic(ImageDataFactory.create("./assets/gov-br-logo.png")) //
                .setPageRect(rectangle) //
                .setPageNumber(1);

        logger.info("buildAppearence | init");
    }
}
