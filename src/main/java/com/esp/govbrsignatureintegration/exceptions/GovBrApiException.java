package com.esp.govbrsignatureintegration.exceptions;

public class GovBrApiException extends RuntimeException {
    private ErrorMessage errorMessage;

    public GovBrApiException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}
