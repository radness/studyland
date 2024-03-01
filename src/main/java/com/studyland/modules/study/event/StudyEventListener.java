package com.studyland.modules.study.event;

import com.studyland.infra.config.AppProperties;
import com.studyland.infra.mail.EmailMessage;
import com.studyland.infra.mail.EmailService;
import com.studyland.modules.account.Account;
import com.studyland.modules.account.AccountPredicates;
import com.studyland.modules.account.AccountRepository;
import com.studyland.modules.notification.Notification;
import com.studyland.modules.notification.NotificationRepository;
import com.studyland.modules.notification.NotificationType;
import com.studyland.modules.study.Study;
import com.studyland.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Transactional()
@Component
@Async
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            // 이메일 전송
            if (account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(account, study);
            }
            // 알림 생성
            if (account.isStudyCreatedByWeb()) {
                saveStudyCreatedNotification(account, study);
            }
        });
    }

    private void saveStudyCreatedNotification(Account account, Study study) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(study.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Account account, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생성되었습니다.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디랜드, '" + study.getTitle() + "' 스터디가 생성되었습니다'")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}
