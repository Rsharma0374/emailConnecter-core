package com.emailConnecter.utility;


import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.response.Payload;
import com.emailConnecter.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.*;
import java.util.Collections;
import java.util.Properties;

public class ResponseUtility {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtility.class);

    public static BaseResponse getBaseResponse(HttpStatus httpStatus, Object buzResponse) {
        logger.info("Inside getBaseResponse method");

        if (null == buzResponse)
            buzResponse = Collections.emptyMap();

        return BaseResponse.builder()
                .payload(new Payload<>(buzResponse))
                .status(
                        Status.builder()
                                .statusCode(httpStatus.value())
                                .statusValue(httpStatus.name()).build())
                .build();
    }

    public static String encryptThisString(String input) {


        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called to calculate message digest of the input string returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40 bit
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            logger.error("Exception occurred at sha conversion due to - ", e);
            throw new RuntimeException(e);
        }

    }

    public static Properties fetchProperties(String userAuthPropertiesPath) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(userAuthPropertiesPath));
            return properties;
        } catch (IOException e) {
            logger.error("Exception occurred while getting user auth config with probable cause - ", e);
            return null;
        }
    }

    public static long dayEndExpiryTime() {
        // Current time
        LocalDateTime now = LocalDateTime.now();

        // End of the day (11:59:59 PM)
        LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

        // Calculate duration in seconds
        return Duration.between(now, endOfDay).getSeconds();
    }
}

