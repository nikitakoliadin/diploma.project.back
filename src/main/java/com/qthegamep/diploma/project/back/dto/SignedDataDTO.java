package com.qthegamep.diploma.project.back.dto;

import java.util.Objects;

public class SignedDataDTO {

    private String signedData;

    public String getSignedData() {
        return signedData;
    }

    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedDataDTO that = (SignedDataDTO) o;
        return Objects.equals(signedData, that.signedData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signedData);
    }

    @Override
    public String toString() {
        return "SignedDataDTO{" +
                "signedData='" + signedData + '\'' +
                '}';
    }
}
