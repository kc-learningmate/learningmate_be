package org.kc5.learningmate.domain.auth.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.common.constants.EmailConstants;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.kc5.learningmate.domain.auth.properties.AuthProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final AuthProperties authProperties;

    public void sendAuthCodeMail(String to, String authCode) {
        String body = createEmailBody("auth-code-email", "authCode", authCode);
        sendMail(to, EmailConstants.AUTH_CODE_SUBJECT, body);
    }

    public void sendResetPasswdMail(String to, String authToken) {
        String redirectUrl = UriComponentsBuilder.fromUriString(authProperties.getBaseUrl())
                                                 .path(authProperties.getPasswdResetUrl())
                                                 .queryParam("authToken", authToken)
                                                 .build()
                                                 .toUriString();

        log.info("redirect URL: {}", redirectUrl);

        String body = createEmailBody("passwd-reset-email", "redirectUrl", redirectUrl);
        sendMail(to, EmailConstants.PASSWD_RESET_SUBJECT, body);
    }

    private String createEmailBody(String templateName, String variableName, String variableValue) {
        Context context = new Context();
        context.setVariable(variableName, variableValue);
        return templateEngine.process(templateName, context);
    }

    private void sendMail(String to, String subject, String body) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CommonException(ErrorCode.SEND_EMAIL_FAIL);
        }

    }
}
