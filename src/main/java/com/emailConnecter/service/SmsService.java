package com.emailConnecter.service;

import com.emailConnecter.request.SmsRequest;
import com.emailConnecter.response.BaseResponse;

public interface SmsService {
    BaseResponse sendSms(SmsRequest smsRequest);
}
