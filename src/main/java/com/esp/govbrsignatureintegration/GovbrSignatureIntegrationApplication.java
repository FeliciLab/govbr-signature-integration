package com.esp.govbrsignatureintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class GovbrSignatureIntegrationApplication {
    @GetMapping("/")
    public String index(@RequestParam("code") String code){
        return code;
    }

    public static void main(String[] args) {
        SpringApplication.run(GovbrSignatureIntegrationApplication.class, args);
    }

}
