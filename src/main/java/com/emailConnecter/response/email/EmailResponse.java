package com.emailConnecter.response.email;

public class EmailResponse {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MailResponse{" +
                "status='" + status + '\'' +
                '}';
    }
}
