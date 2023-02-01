package com.esp.govbrsignatureintegration.signature;

import com.esp.govbrsignatureintegration.services.GetCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Gerenciador de um certificado gerado pelo gov.br.
 */
public class CertificataManager {
    private static final Logger logger = LoggerFactory.getLogger(CertificataManager.class);

    private GetCertificateService getCertificateService;
    private String token; // token para assinar o documento

    public CertificataManager(GetCertificateService getCertificateService, String token) {
        this.getCertificateService = getCertificateService;
        this.token = token;
    }

    /**
     * Busca no certificado gerado pelo nome do criador do certificado.
     * Usamos isso para setar o nome do criador da assinatura digital.
     * @return
     * @throws CertificateException
     */
    public String getCertificateCreatorName() throws CertificateException {
        logger.info("getCertificateCreatorName | init");

        byte[] certificateBytes = getCertificateService.getCertificadoPublico(this.token);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certificateBytes));

        String getCertificateCreatorName = certificate.getSubjectX500Principal().getName().split("=")[1];

        logger.info("getCertificateCreatorName | final");

        return getCertificateCreatorName;
    }
}
