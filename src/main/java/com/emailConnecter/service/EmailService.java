package com.emailConnecter.service;

import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.response.BaseResponse;

public interface EmailService {

    BaseResponse sendMail(EmailRequest emailRequest);
}
