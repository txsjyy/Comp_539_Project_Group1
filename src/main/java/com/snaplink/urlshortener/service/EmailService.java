// src/main/java/com/snaplink/urlshortener/service/EmailService.java
package com.snaplink.urlshortener.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.data.model.templates.TemplatedMessage;
import com.postmarkapp.postmark.client.exception.InvalidMessageException;
import com.postmarkapp.postmark.client.exception.PostmarkException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${postmark.server-token}")
    private String serverToken;

    @Value("${postmark.template.signup-id}")
    private int signupTemplateId;

    @Value("${postmark.template.reset-password-id}")
    private int resetPasswordTemplateId;

    @Value("${app.mail.from}")
    private String from;

    // ← all the variables your template references
    @Value("${app.product-name}")
    private String productName;

    @Value("${app.login-url}")
    private String loginUrl;

    @Value("${app.support-email}")
    private String supportEmail;

    @Value("${app.live-chat-url}")
    private String liveChatUrl;

    @Value("${app.sender-name}")
    private String senderName;

    @Value("${app.help-url}")
    private String helpUrl;

    @Value("${app.trial.length-days}")
    private int trialLengthDays;

    @Value("${app.support-url}")
    private String supportUrl;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ApiClient client() {
        return Postmark.getApiClient(serverToken);
    }

    /**
     * Send the “Welcome” email via your Postmark signup template.
     */
    public void sendWelcomeEmail(String to, String username) throws PostmarkException, IOException {
        // compute trial start/end
        LocalDate start = LocalDate.now();
        LocalDate end   = start.plusDays(trialLengthDays);

        // build the model exactly matching your template variables
        Map<String,Object> model = new HashMap<>();
        model.put("name",              username);
        model.put("product_name",      productName);
        model.put("action_url",        loginUrl);       // “Do this Next” button
        model.put("login_url",         loginUrl);
        model.put("username",          username);
        model.put("trial_length",      trialLengthDays);
        model.put("trial_start_date",  start.format(DATE_FMT));
        model.put("trial_end_date",    end.format(DATE_FMT));
        model.put("support_email",     supportEmail);
        model.put("live_chat_url",     liveChatUrl);
        model.put("sender_name",       senderName);
        model.put("help_url",          helpUrl);

        TemplatedMessage msg = new TemplatedMessage(from, to);
        msg.setTemplateId(signupTemplateId);
        msg.setTemplateModel(model);

        try {
            MessageResponse resp = client().deliverMessageWithTemplate(msg);
            if (resp.getErrorCode() != 0) { // Assuming getErrorCode() returns 0 for no error
                throw new IllegalStateException(
                    "Welcome email failed: " + resp.getErrorCode() + " – " + resp.getMessage()
                );
            }
        } catch (PostmarkException | IOException e) {
            throw new IllegalStateException("Failed to send welcome email", e);
        }
    }


 /**
     * Sends the reset‑password email via your Postmark template.
     * Throws on any error—no internal logging.
     *
     * @param to               recipient email
     * @param name             user's name ({{name}})
     * @param resetLink        reset URL ({{action_url}})
     * @param operatingSystem  detected OS ({{operating_system}})
     * @param browserName      detected browser ({{browser_name}})
     */
    public void sendPasswordResetEmail(
            String to,
            String name,
            String resetLink,
            String operatingSystem,
            String browserName
    ) throws PostmarkException, IOException {
        TemplatedMessage msg = new TemplatedMessage(from, to);
        msg.setTemplateId(resetPasswordTemplateId);

        Map<String,Object> model = new HashMap<>();
        model.put("name",             name);
        model.put("product_name",     productName);
        model.put("action_url",       resetLink);
        model.put("operating_system", operatingSystem);
        model.put("browser_name",     browserName);
        model.put("support_url",      supportUrl);
        msg.setTemplateModel(model);

        MessageResponse resp = client().deliverMessageWithTemplate(msg);
        if (resp.getErrorCode() != 0) {
            throw new PostmarkException(
                "Reset email failed: code=" + resp.getErrorCode()
                + ", message=" + resp.getMessage()
            );
        }
    }
}
