package com.emailConnecter.controller;

import com.emailConnecter.constants.Constant;
import com.emailConnecter.request.EmailRequest;
import com.emailConnecter.service.AwsSesEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AwsSesEmailService awsSesEmailService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmailRequest validEmailRequest;

    @BeforeEach
    void setUp() {
        validEmailRequest = new EmailRequest();
        validEmailRequest.setTo("rsharma0374@gmail.com");
        validEmailRequest.setSubject("Test Subject");
        validEmailRequest.setMessage("Test Message Content");
    }

    @Test
    @DisplayName("GET /email-connector/welcome - Positive Case")
    void testWelcomeEndpoint() throws Exception {
        mockMvc.perform(get("/email-connector/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome to")))
                .andExpect(content().string(containsString("Email Connector")));
    }

    @Test
    @DisplayName("POST /email-connector/send-mail - Positive Case (Successful Email Send)")
    void testSendEmail_Success() throws Exception {
        String mockMessageId = "msg-id-12345";
        when(awsSesEmailService.sendEmail(any(EmailRequest.class))).thenReturn(mockMessageId);

        mockMvc.perform(post("/email-connector/send-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Constant.SUCCESS))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value(Constant.EMAIL_SENT_SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.messageId").value(mockMessageId));

        verify(awsSesEmailService, times(1)).sendEmail(any(EmailRequest.class));
    }

    @Test
    @DisplayName("POST /email-connector/send-mail - Negative Case (Service Throws Exception)")
    void testSendEmail_ServiceThrowsException() throws Exception {
        String errorMessage = "AWS SES service down";
        when(awsSesEmailService.sendEmail(any(EmailRequest.class)))
                .thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(post("/email-connector/send-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmailRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(Constant.ERROR))
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.message").value(Constant.EMAIL_SENT_FAILED_MESSAGE + errorMessage))
                .andExpect(jsonPath("$.messageId").doesNotExist());

        verify(awsSesEmailService, times(1)).sendEmail(any(EmailRequest.class));
    }

    @Test
    @DisplayName("POST /email-connector/send-mail - Edge Case (Missing subject/message)")
    void testSendEmail_MissingSubjectAndMessage() throws Exception {
        // Without @Valid annotations in EmailController, it forwards the request directly to the service.
        EmailRequest edgeCaseRequest = new EmailRequest();
        edgeCaseRequest.setTo("test@example.com");

        String mockMessageId = "msg-id-edge-case";
        when(awsSesEmailService.sendEmail(any(EmailRequest.class))).thenReturn(mockMessageId);

        mockMvc.perform(post("/email-connector/send-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(edgeCaseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Constant.SUCCESS))
                .andExpect(jsonPath("$.messageId").value(mockMessageId));
    }

    @Test
    @DisplayName("POST /email-connector/send-mail - Boundary Case (Extremely Large Message Body)")
    void testSendEmail_LargeMessageBody() throws Exception {
        String largeMessage = "A".repeat(10000); // 10k characters
        validEmailRequest.setMessage(largeMessage);

        String mockMessageId = "msg-id-large";
        when(awsSesEmailService.sendEmail(any(EmailRequest.class))).thenReturn(mockMessageId);

        mockMvc.perform(post("/email-connector/send-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Constant.SUCCESS))
                .andExpect(jsonPath("$.messageId").value(mockMessageId));
    }

    @Test
    @DisplayName("POST /email-connector/send-mail - Negative Case (Malformed JSON Request)")
    void testSendEmail_MalformedJson() throws Exception {
        String malformedJson = "{ \"to\": \"test@example.com\", \"subject\": \"Test\" "; // Missing closing brace

        mockMvc.perform(post("/email-connector/send-mail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest()); // Spring automatically handles malformed JSON with 400
    }
}
