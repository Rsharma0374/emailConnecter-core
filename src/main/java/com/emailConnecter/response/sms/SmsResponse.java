package com.emailConnecter.response.sms;

import com.emailConnecter.response.OtpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponse {

    private OtpStatus otpStatus;

    private String message;

}
