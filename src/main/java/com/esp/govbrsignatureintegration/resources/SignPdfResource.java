package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.signature.SignatureContainer;
import com.esp.govbrsignatureintegration.signature.SignatureManager;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/signPdf")
public class SignPdfResource {
    @Autowired
    private GetTokenService getTokenService;

    @Autowired
    private AssinarPKCS7Service assinarPKCS7Service;

    /**
     * Rota para assinar um documento PDF.
     *
     * @param code {@link String} que é passada na rota como variável
     * @param pdf {@link MultipartFile} do arquivo
     * @return um arquivo pdf assinado.
     */
    @PostMapping(value = "/{code}", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        try {
            String token = this.getTokenService.getToken(code);

            SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service);

            byte[] outputBytes = signatureManager.getBytesPdfSigned(pdf.getInputStream());

            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rota para assinar um documentos PDF em Lote.
     *
     * @param code {@link String} que é passada na rota como variável.
     * @param pdf {@link MultipartFile} do arquivo.
     * @return Retorna um arquivo zip com os documentos assinados.
     */
    @PostMapping(value = "/lote/{code}", produces = "application/zip")
    public ResponseEntity<InputStreamResource> uploadFilesToSignInLote(@PathVariable String code, @RequestParam MultipartFile[] pdfs) {
        ByteArrayOutputStream zipByteArrayOutputStream = new ByteArrayOutputStream();

        ZipOutputStream zipOutputStream = new ZipOutputStream(zipByteArrayOutputStream);

        try {
            String token = this.getTokenService.getToken(code);

            SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service);

            for (MultipartFile pdf: pdfs) {
                byte[] outputBytes = signatureManager.getBytesPdfSigned(pdf.getInputStream());

                InputStream inputStream = new ByteArrayInputStream(outputBytes);

                ZipEntry zipEntry = new ZipEntry(pdf.getOriginalFilename());

                zipOutputStream.putNextEntry(zipEntry);

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) { zipOutputStream.write(bytes, 0, length); }

                zipOutputStream.closeEntry();

                inputStream.close();
            }

            zipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        byte[] outputBytes = zipByteArrayOutputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", "inline; filename=citiesreport.zip");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
    }
}
