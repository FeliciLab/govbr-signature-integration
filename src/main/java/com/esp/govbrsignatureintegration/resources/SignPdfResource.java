package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

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
            assinatura = this.assinarPKCS7Service.getAssinaturaPKC7(token, hashSHA256(pdf.getInputStream()));

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

    /**
     * TODO: colocar esse método em um local mais apropriado
     * Para gerar um pacote PKCS#7 contendo a assinatura digital de um HASH SHA-256.
     */
    private String hashSHA256(InputStream data) throws GeneralSecurityException, IOException {
        String hashAlgorithm = "SHA256";
        BouncyCastleDigest digest = new BouncyCastleDigest();
        byte[] documentHash = DigestAlgorithms.digest(data, digest.getMessageDigest(hashAlgorithm));
        return Base64.getEncoder().encodeToString(documentHash);
    }
}
