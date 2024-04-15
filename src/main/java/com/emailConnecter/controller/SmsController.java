package com.emailConnecter.controller;

import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.request.SmsRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms-connector")
public class SmsController {
    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @Autowired
    SmsService smsService;

    @PostMapping("/send-sms")
    public ResponseEntity<BaseResponse> sendSms(
            @RequestBody SmsRequest smsRequest) {
        logger.debug("send-sms controller started");
        return new ResponseEntity<>(smsService.sendSms(smsRequest), HttpStatus.OK);    }
}
