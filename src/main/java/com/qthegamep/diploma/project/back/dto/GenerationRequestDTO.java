package com.qthegamep.diploma.project.back.dto;

import java.util.Objects;

public class GenerationRequestDTO {
    private String alias;
    private String pass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerationRequestDTO that = (GenerationRequestDTO) o;
        return Objects.equals(alias, that.alias) &&
                Objects.equals(pass, that.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, pass);
    }

    @Override
    public String toString() {
        return "GenerationRequestDTO{" +
                "alias='" + alias + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
