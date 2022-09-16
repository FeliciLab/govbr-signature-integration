package com.esp.govbrsignatureintegration.resources;

import com.esp.govbrsignatureintegration.services.AssinarPKCS7Service;
import com.esp.govbrsignatureintegration.services.GetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

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
    public String uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        String token = this.getTokenService.getToken(code);

        String assinatura = this.assinarPKCS7Service.getAssinaturaPKC7(token, "");

        System.out.println("assinatura: " + assinatura);

        return assinatura;
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
