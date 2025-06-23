package com.emailConnecter.service.impl;


import com.emailConnecter.config.CacheConfig;
import com.emailConnecter.config.InfisicalConfig;
import com.emailConnecter.constants.Constant;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.request.PortfolioMessageRequest;
import com.emailConnecter.response.BaseResponse;
import com.emailConnecter.response.email.EmailResponse;
import com.emailConnecter.service.EmailService;
import com.emailConnecter.utility.ResponseUtility;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.mail.*;
import jakarta.mail.internet.*;
//import javax.mail.*;
//import javax.mail.internet.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    public static final String BREVO_EMAIL_COUNT = "brevoEmailCount";
    private static final String API_URL = "https://api.brevo.com/v3/smtp/statistics/reports";


    @Override
    public BaseResponse sendMail(EmailRequest emailRequest) {
        logger.warn("Inside sendMail");

        BaseResponse baseResponse = null;
        EmailResponse emailResponse = new EmailResponse();
        try {
            int brevoEmailCount = 0;
            Object brevoEmailCountConfig = CacheConfig.get(BREVO_EMAIL_COUNT);
            logger.warn("Brevo count config: {}", brevoEmailCountConfig);
            if (brevoEmailCountConfig == null) {
                logger.warn("Brevo count config is null");
                addingCacheConfig();

                brevoEmailCountConfig = CacheConfig.get(BREVO_EMAIL_COUNT);
                logger.warn("Brevo email count config: {}", brevoEmailCountConfig);
            }
            if (brevoEmailCountConfig instanceof Integer) {
                brevoEmailCount = (Integer) brevoEmailCountConfig;
            } else {
                brevoEmailCount = getBrevoEmailCountByApi();
            }
            if (brevoEmailCount >=0 && brevoEmailCount <= 250) { //send email by brevo
                sendEmailBrevo(emailRequest, emailResponse, brevoEmailCount);
            } else {
                sendEmailGuardianService(emailRequest, emailResponse);
            }
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, emailResponse);

        } catch (Exception e) {
            logger.error("Exception Occurred at the time sending email with probable cause- ", e);
            emailResponse.setStatus("Failed");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, emailResponse);
        }
        return baseResponse;

    }

    private static void addingCacheConfig() throws Exception {
        Map properties = InfisicalConfig.fetchConfig("Email");


        if (properties != null) {
            logger.warn("Properties is not null");
            Map<String, String> guardianSmtp = new HashMap<>();
            Map<String, String> brevoSmtp = new HashMap<>();

            //Guardian Service SMTP
            guardianSmtp.put("host", properties.get(Constant.GSERVICE_HOST).toString());
            guardianSmtp.put("port", properties.get(Constant.GSERVICE_PORT).toString());
            guardianSmtp.put("username", properties.get(Constant.GSERVICE_USERNAME).toString());
            guardianSmtp.put("password", properties.get(Constant.GSERVICE_PASSWORD).toString());

            //Brevo service SMTP
            brevoSmtp.put("host", properties.get(Constant.BREVO_HOST).toString());
            brevoSmtp.put("port", properties.get(Constant.BREVO_PORT).toString());
            brevoSmtp.put("username", properties.get(Constant.BREVO_USERNAME).toString());
            brevoSmtp.put("password", properties.get(Constant.BREVO_PASSWORD).toString());

            long expiryTime = ResponseUtility.dayEndExpiryTime();
            CacheConfig.put("guardianSmtp", guardianSmtp, expiryTime);
            CacheConfig.put("brevoSmtp", brevoSmtp, expiryTime);
            CacheConfig.CACHE.put(Constant.FROM, properties.get(Constant.FROM).toString());
            CacheConfig.CACHE.put(Constant.BREVO_API_KEY, properties.get(Constant.BREVO_API_KEY).toString());
        }
    }

    private int getBrevoEmailCountByApi() {
        String brevoStatistics = getCurrentDayStatisticsBrevo();
        if (StringUtils.isNotBlank(brevoStatistics)) {
            JSONObject jsonObject = new JSONObject(brevoStatistics);
            if (jsonObject.has("reports")) {
                JSONArray reportArray = jsonObject.getJSONArray("reports");
                if (!reportArray.isEmpty()) {
                    JSONObject report = reportArray.getJSONObject(0);
                    if (report.has("requests")) {
                        int count = report.getInt("requests");
                        CacheConfig.CACHE.put(BREVO_EMAIL_COUNT, count);
                        return count;
                    }
                } else {
                    return 0;
                }
            }
        }
        return -1;
    }

    private void sendEmailBrevo(EmailRequest emailRequest, EmailResponse emailResponse, int brevoEmailCount) throws Exception {

        Map<String, String> brevoSmtp = (Map<String, String>) CacheConfig.get("brevoSmtp");
        if (null == brevoSmtp || brevoSmtp.isEmpty()) {
            sendEmailGuardianService(emailRequest, emailResponse);
            return;
        }
        String host = brevoSmtp.get("host");
        String port = brevoSmtp.get("port");
        String username = brevoSmtp.get("username");
        String password = brevoSmtp.get("password");
        String from = (String) CacheConfig.get(Constant.FROM);

        // Set up properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS

        // Authenticate
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Create the email
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(from));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRequest.getTo()));
        mimeMessage.setSubject(emailRequest.getSubject());
        mimeMessage.setText(emailRequest.getMessage());
        CacheConfig.CACHE.put(BREVO_EMAIL_COUNT, (brevoEmailCount + 1));
        // Send the email
        Transport.send(mimeMessage);
        logger.info("Send Success.....");
        emailResponse.setStatus("Success");
    }

    private static void sendEmailGuardianService(EmailRequest emailRequest, EmailResponse emailResponse) throws Exception {

        Map<String, String> guardianSmtp = (Map<String, String>) CacheConfig.get("guardianSmtp");
        if (null == guardianSmtp || guardianSmtp.isEmpty()) {
            addingCacheConfig();
            guardianSmtp = (Map<String, String>) CacheConfig.get("guardianSmtp");
        }
        String host = guardianSmtp.get("host");
        String port = guardianSmtp.get("port");
        String from = guardianSmtp.get("username");
        String password = guardianSmtp.get("password");

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");

        properties.put("mail.smtp.ssl.enable", "true");          // ✅ Enable implicit SSL
        properties.put("mail.smtp.starttls.enable", "false");    // ✅ Disable STARTTLS
        properties.put("mail.transport.protocol", "smtp");

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
    }

    @Override
    public BaseResponse sendPortfolioQuery(PortfolioMessageRequest portfolioMessageRequest) {
        BaseResponse baseResponse = null;
        EmailResponse emailResponse = new EmailResponse();
        EmailRequest emailRequest = new EmailRequest();
        try {

            String subject = "Portfolio message from " + portfolioMessageRequest.getName();
            String message = "You have a message from " + portfolioMessageRequest.getQueryEmailId() +". \n" + "The message is: " + portfolioMessageRequest.getMessage();
            String toEmail = "rsharma0374@gmail.com";
            emailRequest.setMessage(message);
            emailRequest.setTo(toEmail);
            emailRequest.setSubject(subject);


            int brevoEmailCount = 0;
            Object brevoEmailCountConfig = CacheConfig.get(BREVO_EMAIL_COUNT);
            if (brevoEmailCountConfig instanceof Integer) {
                brevoEmailCount = (Integer) brevoEmailCountConfig;
            } else {
                brevoEmailCount = getBrevoEmailCountByApi();
            }
            if (brevoEmailCount >=0 && brevoEmailCount <= 250) { //send email by brevo
                sendEmailBrevo(emailRequest, emailResponse, brevoEmailCount);
            } else {
                sendEmailGuardianService(emailRequest, emailResponse);
            }


            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, emailResponse);

        } catch (Exception e) {
            logger.error("Exception Occurred at the time sending email with probable cause- ", e);
            emailResponse.setStatus("Failed");
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, emailResponse);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse getCurrentDayStatistics() {
        logger.info("Get Current Day Statistics");
        BaseResponse baseResponse = null;
        try {
            String  currentDayStatistics = getCurrentDayStatisticsBrevo();
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.OK, currentDayStatistics);
            
        } catch (Exception e) {
            logger.error("Exception Occurred at the time getting current day statistics ", e);
            baseResponse = ResponseUtility.getBaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, Collections.singleton(e));

        }
        return baseResponse;
    }

    private String getCurrentDayStatisticsBrevo() {
        StringBuilder response = new StringBuilder();
        try {
            // Define the start and end date for the query (e.g., today)
            String startDate = getCurrentDateYYYYMMDD();
            String endDate = getCurrentDateYYYYMMDD();

            // Create the API URL with date filter
            String apiUrlWithParams = String.format("%s?startDate=%s&endDate=%s", API_URL, startDate, endDate);

            // Send GET request to Brevo API
            URL url = new URL(apiUrlWithParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("api-key", (String) CacheConfig.get(Constant.BREVO_API_KEY)); // Add the API key for authorization

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            logger.info("Response from Brevo API: " + response.toString());

        } catch (Exception e) {
            logger.error("Exception Occurred at the time getting current day statistics ", e);
        }
        return response.toString();
    }

    private String getCurrentDateYYYYMMDD() {
        // Get current date and time in IST (or your local timezone)
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // Create a SimpleDateFormat object for GMT
        SimpleDateFormat gmtFormatter = new SimpleDateFormat("yyyy-MM-dd");
        gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Format the date in GMT
        return gmtFormatter.format(date);
    }

}
