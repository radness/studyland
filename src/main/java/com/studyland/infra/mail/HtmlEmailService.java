package com.studyland.infra.mail;

import com.studyland.infra.mail.EmailMessage;
import com.studyland.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // 두번째 파라미터 : email 에 첨부파일을 보낼거면 true
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo());
            mimeMessageHelper.setSubject(emailMessage.getSubject());
            mimeMessageHelper.setText(emailMessage.getMessage(), false);
            javaMailSender.send(mimeMessage);
            // 보냈다고 표시
            log.info("sent email: {}", emailMessage.getMessage());
        } catch (MessagingException e) {
            log.error("failed to send email.", e);
            throw new RuntimeException(e); // 에러 던지기
        }
    }
}
