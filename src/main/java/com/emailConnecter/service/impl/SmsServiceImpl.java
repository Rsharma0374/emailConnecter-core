package com.emailConnecter.service.impl;

import com.emailConnecter.config.TwilioConfig;
import com.emailConnecter.request.SmsRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.response.OtpStatus;
import com.emailConnecter.response.sms.SmsResponse;
import com.emailConnecter.service.SmsService;
import com.emailConnecter.utility.ResponseUtility;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);


    @Autowired
    private TwilioConfig twilioConfig;

    @Override
    public BaseResponse sendSms(SmsRequest smsRequest) {
        SmsResponse smsResponse = null;
        BaseResponse baseResponse = null;
        System.setProperty("aws_sns_key", "test");
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        logger.debug(System.getProperty("aws_sns_key"));
        try {
            String otp = generateOtp();
            String otpMessage = "Dear Customer, Your OTP is ##" + otp + "##. Use this passcose to complete your transaction. Thank You.";
            Message message = Message
                    .creator(new PhoneNumber(smsRequest.getPhoneNumber()),
                            new PhoneNumber(twilioConfig.getTrialNumber()),
                            otpMessage)
                    .create();
            smsResponse = new SmsResponse(OtpStatus.DELIVERED, otpMessage);
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, smsResponse);
        } catch (Exception e) {
            logger.error("Exception occurred while sending otp with probable cause- ", e);
            smsResponse = new SmsResponse(OtpStatus.FAILED, e.getMessage());
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, smsResponse);
        }
        return baseResponse;
    }

    private String generateOtp() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

}
