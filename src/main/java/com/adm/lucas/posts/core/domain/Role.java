package com.adm.lucas.posts.core.domain;

public enum Role {

    ACTIVATED("activated"),
    DEMO("demo"),
    DEACTIVATED("deactivated");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}