package com.qthegamep.diploma.project.back.dto;

import java.util.Objects;

public class EncryptedResponseDTO {
    private String encryptedData;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncryptedResponseDTO that = (EncryptedResponseDTO) o;
        return Objects.equals(encryptedData, that.encryptedData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedData);
    }

    @Override
    public String toString() {
        return "EncryptedResponseDTO{" +
                "encryptedData='" + encryptedData + '\'' +
                '}';
    }
}
