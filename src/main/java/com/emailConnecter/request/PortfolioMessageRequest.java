package com.emailConnecter.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortfolioMessageRequest {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Email")
    private String queryEmailId;

    @JsonProperty("Message")
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueryEmailId() {
        return queryEmailId;
    }

    public void setQueryEmailId(String queryEmailId) {
        this.queryEmailId = queryEmailId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

