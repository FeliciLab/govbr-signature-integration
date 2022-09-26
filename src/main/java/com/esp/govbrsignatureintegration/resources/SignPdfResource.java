package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.signature.SignatureManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/signPdf")
public class SignPdfResource {
    private static final Logger logger = LoggerFactory.getLogger(SignPdfResource.class);

    @Autowired
    private GetTokenService getTokenService;

    @Autowired
    private AssinarPKCS7Service assinarPKCS7Service;

    /**
     * Rota para assinar um documento PDF.
     *
     * @param code {@link String} que é passada na rota como variável
     * @param pdf  {@link MultipartFile} do arquivo
     * @return um arquivo pdf assinado.
     */
    @PostMapping(value = "/{code}", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        logger.info("uploadFilesToSign | code: {}", code);
        try {
            String token = this.getTokenService.getToken(code);

            SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service);

            byte[] outputBytes = signatureManager.getBytesPdfSigned(pdf.getInputStream());

            return ResponseEntity.ok().body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de assinatura digital");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de I/O");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getStatusText());
        }
    }

    /**
     * Rota para assinar um documentos PDF em Lote.
     *
     * @param code {@link String} que é passada na rota como variável.
     * @param pdfs Array de {@link MultipartFile} dos arquivos.
     * @return Retorna um arquivo zip com os documentos assinados.
     */
    @PostMapping(value = "/lote/{code}", produces = "application/zip")
    public ResponseEntity<InputStreamResource> uploadFilesToSignInLote(@PathVariable String code, @RequestParam MultipartFile[] pdfs) {
        logger.info("uploadFilesToSignInLote | code: {}", code);

        ByteArrayOutputStream zipByteArrayOutputStream = new ByteArrayOutputStream();

        ZipOutputStream zipOutputStream = new ZipOutputStream(zipByteArrayOutputStream);

        try {
            String token = this.getTokenService.getToken(code);

            SignatureManager signatureManager = new SignatureManager(token, this.assinarPKCS7Service);

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
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de assinatura digital");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro de I/O");
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getStatusText());
        }

        byte[] outputBytes = zipByteArrayOutputStream.toByteArray();

        return ResponseEntity.ok().body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
    }
}
