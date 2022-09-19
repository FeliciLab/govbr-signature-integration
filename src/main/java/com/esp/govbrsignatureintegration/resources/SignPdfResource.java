package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.esp.govbrsignatureintegration.utils.Util;
import com.itextpdf.kernel.pdf.PdfReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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
     * @param code
     * @param pdf
     * @return
     */
    @GetMapping("/{code}")
    public ResponseEntity<InputStreamResource> uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        String token = this.getTokenService.getToken(code);

        String assinatura = null;

        try {
            String hash = Util.generateHashSHA256(pdf.getInputStream());

            assinatura = this.assinarPKCS7Service.getAssinaturaPKC7(token, hash);

            PdfReader pdfReader = new PdfReader(pdf.getInputStream());

            // TODO: continuar aqui
            // Como passar o pdf como retorno????

            System.out.println("assinatura: " + assinatura);

            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

            // TODO: por enquanto estou retornando o mesmo arquivo para testar
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(pdf.getBytes())));

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Toda para testes
     * @return
     */
    @GetMapping("/test")
    public String test() {
        return "teste";
    }

}
