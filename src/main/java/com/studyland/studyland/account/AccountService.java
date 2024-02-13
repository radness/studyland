package com.studyland.studyland.account;

import com.studyland.studyland.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpForm signUpForm) {
        // 리팩토링 : ctrl + alt + m
        Account newAccount = saveNewAccount(signUpForm); // 새로운 계정 저장
        // email 보내기
        newAccount.generateEmailCheckToken(); // 토큰 생성
        sendSignUpConfirmEmail(newAccount); // 확인 이메일 보내기
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // TODO encoding 해야함.
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newAccount.getEmail());
        simpleMailMessage.setSubject("스터디랜드, 회원 가입 인증"); // 제목
        // 본문, 만들어보낸 토큰값을 가져와서 매개변수로 전달.
        simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "%email=" + newAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

}
