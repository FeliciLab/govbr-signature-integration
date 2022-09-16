package com.esp.govbrsignatureintegration.resources;

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

    @GetMapping("/{code}")
    public String uploadFilesToSign(@PathVariable String code, @RequestParam MultipartFile pdf) {
        String token = this.getTokenService.getToken(code);

        System.out.println("token: " + token);

        return code;
    }

    @GetMapping("/test")
    public String test() {
        return "teste";
    }

}
