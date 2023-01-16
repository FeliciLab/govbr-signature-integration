package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.utils.Util;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.svg.converter.SvgConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

public class SignatureManager {
    private static final Logger logger = LoggerFactory.getLogger(SignatureManager.class);

    private String imgQRCodeSource;

    private String imgESPLogoSource;

    private static final int ESTIMATED_SIZE = 8192; // Tamanho estimado da assinatura.
    private String token; // token para assinar o documento
    private AssinarPKCS7Service assinarPKCS7Service; // webcliente para fazer a request

    public SignatureManager(String token, AssinarPKCS7Service assinarPKCS7Service, String imgESPLogoSource, String imgQRCodeSource) {
        this.token = token;
        this.assinarPKCS7Service = assinarPKCS7Service;
        this.imgESPLogoSource = imgESPLogoSource;
        this.imgQRCodeSource = imgQRCodeSource;
    }

    // Usado para gerar os bytes de um arquivo assinado
    public byte[] getBytesPdfSigned(InputStream pdfInputStream) throws IOException, GeneralSecurityException {
        logger.info("getBytesPdfSigned | init");

        PdfReader pdfReader = addImages(pdfInputStream);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfSigner pdfSigner = new PdfSigner(pdfReader, byteArrayOutputStream, new StampingProperties());

        SignatureContainer signatureContainer = new SignatureContainer(this.token, this.assinarPKCS7Service);

        pdfSigner.setSignDate(Calendar.getInstance());

        pdfSigner.setFieldName("Marcelo Alcantara Holanda");

        buildAppearence(pdfSigner);

        pdfSigner.signExternalContainer(signatureContainer, ESTIMATED_SIZE);

        byte[] outputBytes = byteArrayOutputStream.toByteArray();

        logger.info("getBytesPdfSigned | final");

        return outputBytes;
    }

    /**
     * Adiciona imagens no documento.
     *
     * @param pdfInputStream
     * @return
     * @throws IOException
     */
    private PdfReader addImages(InputStream pdfInputStream) throws IOException {
        ByteArrayOutputStream baosOfOutput = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(baosOfOutput));
        Document document = new Document(pdfDoc);

        // Dimensões de uma página A4 rotacionada
        Rectangle pageDimensions = PageSize.A4.rotate();
        float pageWidth = pageDimensions.getWidth();
        float pageHeight = pageDimensions.getHeight();

        // Adiciona imagem QRcode
        Image imgQRcode = SvgConverter.convertToImage(new FileInputStream(this.imgQRCodeSource), pdfDoc).setFixedPosition(1, pageWidth - 150f, pageHeight - 200f);

        document.add(imgQRcode);
        document.close();

        InputStream in = new ByteArrayInputStream(baosOfOutput.toByteArray());
        PdfReader pdfReader = new PdfReader(in);
        return pdfReader;
    }

    /**
     * Construir a aparência da assinatura no pdf.
     *
     * @param appearance instância de @{@link PdfSignatureAppearance}
     * @throws MalformedURLException
     */
    private void buildAppearence(PdfSigner pdfSigner) throws IOException, MalformedURLException {
        logger.info("buildAppearence | init");

        PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();

        // Dimensões de uma página A4 rotacionada
        Rectangle pageDimensions = PageSize.A4.rotate();

        float pageWidth = pageDimensions.getWidth();

        float rectangleWidth = 400f;
        float rectangleHeigth = 40f;

        float rectangleX = (pageWidth - rectangleWidth - 90f) / 2;
        float rectangleY = rectangleHeigth + 120f;

        Rectangle rectangle = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeigth);

        String content = Util.getPdfSignatureAppearanceContent(appearance);

        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION) // Assinatura gráfica e com descrição
                .setLayer2Text(content) // setando conteudo da aparência
                .setLayer2FontSize(10f) // setando tamanho da fonte no layer2
                .setSignatureGraphic(ImageDataFactory.create(this.imgESPLogoSource)) // Imagem lateral da assinatura
                .setPageRect(rectangle) // setando o retangulo
                .setPageNumber(1); // setando a página

        logger.info("buildAppearence | init");
    }
}
