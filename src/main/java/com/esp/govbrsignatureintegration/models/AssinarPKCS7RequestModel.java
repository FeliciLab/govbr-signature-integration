package com.esp.govbrsignatureintegration.models;

public class AssinarPKCS7RequestModel {
    private String hashBase64;

    public AssinarPKCS7RequestModel(String hashBase64) {
        this.hashBase64 = hashBase64;
    }

    public String getHashBase64() {
        return hashBase64;
    }

    public void setHashBase64(String hashBase64) {
        this.hashBase64 = hashBase64;
    }
}
