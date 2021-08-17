package com.qthegamep.diploma.project.back.dto;

import java.util.Objects;

public class SignResponseDTO {

    @Override
    public String toString() {
        return "SignResponseDTO{" +
                "sign='" + sign + '\'' +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignResponseDTO that = (SignResponseDTO) o;
        return sign.equals(that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign);
    }

    private String sign;

}
