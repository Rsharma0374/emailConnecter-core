package com.emailConnecter.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

/**
 * Applies common PII and secret redaction to every formatted log message.
 */
public class SensitiveDataMaskingConverter extends ClassicConverter {

    private static final Pattern EMAIL = Pattern.compile(
            "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SENSITIVE_FIELD = Pattern.compile(
            "(?i)(to|recipient|recipientEmail|subject|message|body|token|password)"
                    + "(\\s*[:=]\\s*)([^,}\\s]+)");

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null || message.isEmpty()) {
            return message;
        }
        return SENSITIVE_FIELD.matcher(EMAIL.matcher(message)
                        .replaceAll("[REDACTED_EMAIL]"))
                .replaceAll("$1$2[REDACTED]");
    }
}
