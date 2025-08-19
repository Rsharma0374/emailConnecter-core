package com.emailConnecter.controller;

import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.request.PortfolioMessageRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);


    @Autowired
    EmailService emailService;

    @GetMapping("/welcome")
    public String welcome() {
        logger.warn("Welcome to Email Connector");
        return "   ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥\n" +
                "  ♥                                                 ♥\n" +
                " ♥                  Welcome to                       ♥\n" +
                " ♥               Email Connector                     ♥\n" +
                "  ♥                                                 ♥\n" +
                "   ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥ ♥\n";
    }

    @PostMapping("/send-mail")
    public ResponseEntity<BaseResponse> sendEmail(
            @RequestBody EmailRequest emailRequest) {
        logger.debug("send-mail controller started");
        return new ResponseEntity<>(emailService.sendMail(emailRequest), HttpStatus.OK);
    }

    @PostMapping("/send-portfolio-message")
    public ResponseEntity<BaseResponse> sendEmail(
            @RequestBody PortfolioMessageRequest portfolioMessageRequest) {
        logger.debug("send-portfolio-message controller started");
        return new ResponseEntity<>(emailService.sendPortfolioQuery(portfolioMessageRequest), HttpStatus.OK);
    }

    @GetMapping("/get-current-day-statistics")
    public ResponseEntity<BaseResponse> getCurrentDayStatistics() {
        logger.debug("get-current-day-statistics controller started");
        return new ResponseEntity<>(emailService.getCurrentDayStatistics(), HttpStatus.OK);
    }
}
