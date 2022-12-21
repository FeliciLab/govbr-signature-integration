package com.esp.govbrsignatureintegration.controllers;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.signature.SignatureManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/signPdf")
public class SignPdfController {
    private static final Logger logger = LoggerFactory.getLogger(SignPdfController.class);

    @Autowired
    private GetTokenService getTokenService;

    @Autowired
    private AssinarPKCS7Service assinarPKCS7Service;

    @Value("${govbr.imgRubricSource}")
    private String imgRubricSource;

    @Value("${govbr.imgQRCodeSource}")
    private String imgQRCodeSource;

    /**
     * Rota para assinar um documento PDF.
     *
     * @param code {@link String} que é passada na rota como variável
     * @param pdf  {@link MultipartFile} do arquivo
     * @return um arquivo pdf assinado.
     */
    @PostMapping(value = "/{code}", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) throws IOException, GeneralSecurityException {
        logger.info("uploadFilesToSign | code: {}", code);

        String token = this.getTokenService.getToken(code);

        SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service, this.imgRubricSource, this.imgQRCodeSource);

        byte[] outputBytes = signatureManager.getBytesPdfSigned(pdf.getInputStream());

        return ResponseEntity.ok().body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
    }

    /**
     * Rota para assinar um documentos PDF em Lote.
     *
     * @param code {@link String} que é passada na rota como variável.
     * @param pdfs Array de {@link MultipartFile} dos arquivos.
     * @return Retorna um arquivo zip com os documentos assinados.
     */
    @PostMapping(value = "/lote/{code}", produces = "application/zip")
    public ResponseEntity<InputStreamResource> uploadFilesToSignInLote(@PathVariable String code, @RequestParam MultipartFile[] pdfs) throws IOException, GeneralSecurityException {
        logger.info("uploadFilesToSignInLote | code: {}", code);

        ByteArrayOutputStream zipByteArrayOutputStream = new ByteArrayOutputStream();

        ZipOutputStream zipOutputStream = new ZipOutputStream(zipByteArrayOutputStream);

        String token = this.getTokenService.getToken(code);

        SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service, this.imgRubricSource, this.imgQRCodeSource);

        for (MultipartFile pdf : pdfs) {
            byte[] outputBytes = signatureManager.getBytesPdfSigned(pdf.getInputStream());

            InputStream inputStream = new ByteArrayInputStream(outputBytes);

            ZipEntry zipEntry = new ZipEntry(pdf.getOriginalFilename());

            zipOutputStream.putNextEntry(zipEntry);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }

            zipOutputStream.closeEntry();
            inputStream.close();
        }
        zipOutputStream.close();

        byte[] outputBytes = zipByteArrayOutputStream.toByteArray();

        return ResponseEntity.ok().body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
    }
}
