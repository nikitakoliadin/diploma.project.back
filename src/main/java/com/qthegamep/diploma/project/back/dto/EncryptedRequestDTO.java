package com.qthegamep.diploma.project.back.dto;

import java.util.Objects;

public class EncryptedRequestDTO {

    private String encryptedData;
    private String authData;

    @Override
    public String toString() {
        return "EncryptedRequestDTO{" +
                "encryptedData='" + encryptedData + '\'' +
                ", authData='" + authData + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptedRequestDTO that = (EncryptedRequestDTO) o;
        return Objects.equals(encryptedData, that.encryptedData) &&
                Objects.equals(authData, that.authData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedData, authData);
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

}
