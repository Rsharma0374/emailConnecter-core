package com.emailConnecter.controller;

import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-connector")
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);


    @Autowired
    EmailService emailService;

    @GetMapping("/welcome")
    public String welcome() {
        return "This means email connection application is Up and running";
    }

    @PostMapping("/send-mail")
    public ResponseEntity<BaseResponse> sendEmail(
            @RequestBody EmailRequest emailRequest) {
        logger.debug("send-mail controller started");
        return new ResponseEntity<>(emailService.sendMail(emailRequest), HttpStatus.OK);    }
}
