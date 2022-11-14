package com.esp.govbrsignatureintegration.exceptions;

/**
 * Caso o usuário não possua este nível Ouro ou Prata de identidade na plataforma do govbr.
 */
public class ImproperDigitalIdentityLevelException extends GovBrApiException {
    public ImproperDigitalIdentityLevelException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
