package com.emailConnecter.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailResponseStatus {
    SUCCESS("Success"),
    ERROR("Error");

    private final String value;

    EmailResponseStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
