package com.emailConnecter.service.impl;


import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.request.PortfolioMessageRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.response.email.EmailResponse;
import com.emailConnecter.service.EmailService;
import com.emailConnecter.utility.ResponseUtility;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import org.slf4j.Logger;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${sprig.mail.username}")
    private String from;

    @Value("${sprig.mail.password}")
    private String password;

    @Override
    public BaseResponse sendMail(EmailRequest emailRequest) {

        BaseResponse baseResponse = null;
        EmailResponse emailResponse = new EmailResponse();
        try {

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            session.setDebug(true);

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(emailRequest.getTo())));
            mimeMessage.setSubject(emailRequest.getSubject());
            mimeMessage.setText(emailRequest.getMessage());

            Transport.send(mimeMessage);
            logger.info("Send Success.....");
            emailResponse.setStatus("Success");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, emailResponse);

        } catch (Exception e) {
            logger.error("Exception Occurred at the time sending email with probable cause- ", e);
            emailResponse.setStatus("Failed");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, emailResponse);
        }
        return baseResponse;

    }

    @Override
    public BaseResponse sendPortfolioQuery(PortfolioMessageRequest portfolioMessageRequest) {
        BaseResponse baseResponse = null;
        EmailResponse emailResponse = new EmailResponse();
        try {

            String subject = "Portfolio message from " + portfolioMessageRequest.getName();
            String message = "You have a message from " + portfolioMessageRequest.getQueryEmailId() +". \n" + "The message is: " + portfolioMessageRequest.getMessage();
            String toEmail = "rsharma0374@gmail.com";
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            session.setDebug(true);

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(toEmail)));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            logger.info("Send Success.....");
            emailResponse.setStatus("Success");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, emailResponse);

        } catch (Exception e) {
            logger.error("Exception Occurred at the time sending email with probable cause- ", e);
            emailResponse.setStatus("Failed");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, emailResponse);
        }
        return baseResponse;
    }

}
