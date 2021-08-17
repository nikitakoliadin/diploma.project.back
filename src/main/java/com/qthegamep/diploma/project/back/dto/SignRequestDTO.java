package com.qthegamep.diploma.project.back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SignRequestDTO {

    @JsonProperty(value = "reqData")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignRequestDTO that = (SignRequestDTO) o;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "SignRequestDTO{" +
                "data='" + data + '\'' +
                '}';
    }
}
