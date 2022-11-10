package com.esp.govbrsignatureintegration.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.GeneralSecurityException;
import java.util.Date;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleAccessDeniedException(AccessDeniedException exception) {
        logger.info("AppExceptionHandler | {}", exception.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage(), "Acesso não autorizado");

        return errorMessage;
    }

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleWebClientResponseException(WebClientResponseException exception) {
        logger.info("AppExceptionHandler | {}", exception.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage(), "Acesso não autorizado");

        return errorMessage;
    }

    @ExceptionHandler(GeneralSecurityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleGeneralSecurityException(GeneralSecurityException exception) {
        logger.info("AppExceptionHandler | {}", exception.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage(), "Acesso não autorizado");

        return errorMessage;
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIOException(IOException exception) {
        logger.info("AppExceptionHandler | {}", exception.getMessage());

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage(), "Acesso não autorizado");

        return errorMessage;
    }
}
