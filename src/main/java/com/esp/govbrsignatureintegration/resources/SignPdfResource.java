package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.signature.SignatureContainer;
import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.utils.Util;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/signPdf")
public class SignPdfResource {
    @Autowired
    private GetTokenService getTokenService;

    @Autowired
    private AssinarPKCS7Service assinarPKCS7Service;

    /**
     * TODO: colocar documentação
     *
     * @param code
     * @param pdf
     * @return
     */
    @GetMapping("/{code}")
    public ResponseEntity<InputStreamResource> uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        try {
            PdfReader pdfReader = new PdfReader(pdf.getInputStream());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            String token = this.getTokenService.getToken(code);

            String hash = Util.generateHashSHA256(pdf.getInputStream());

            String assinatura  = this.assinarPKCS7Service.getAssinaturaPKC7(token, hash);

            PdfSigner pdfSigner = new PdfSigner(pdfReader, byteArrayOutputStream, new StampingProperties());

            Rectangle rectangle = new Rectangle(50, 50, 200, 50);

            PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();

            // Mudando as Captions
            appearance.setReasonCaption("Razão: ");
            appearance.setLocationCaption("Localização: ");

            SignatureContainer signatureContainer = new SignatureContainer(assinatura);

            appearance
                    .setReason("SIGN.GOV.BR")
                    .setLocation("ESP - Escola de Saúde Pública do CE")
                    .setPageRect(rectangle)
                    .setPageNumber(1);

            pdfSigner.setFieldName("Assinatura ESP");

            pdfSigner.signExternalContainer(signatureContainer, 8192);

            byte[] outputBytes = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();

            HttpHeaders headers = new HttpHeaders();

            // headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

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
}
