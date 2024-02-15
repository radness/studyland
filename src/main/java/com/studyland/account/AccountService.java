package com.studyland.account;

import com.studyland.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public Account processNewAccount(SignUpForm signUpForm) {
        // 리팩토링 : ctrl + alt + m
        Account newAccount = saveNewAccount(signUpForm); // 새로운 계정 저장
        // email 보내기
        newAccount.generateEmailCheckToken(); // 토큰 생성
        sendSignUpConfirmEmail(newAccount); // 확인 이메일 보내기
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
//                .password(signUpForm.getPassword())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // Password 인코딩
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(newAccount.getEmail());
        simpleMailMessage.setSubject("스터디랜드, 회원 가입 인증"); // 제목
        // 본문, 만들어보낸 토큰값을 가져와서 매개변수로 전달.
        simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                // Principal 객체
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        // 해당하는 유저가 있는 경우.
        return new UserAccount(account);
    }

    // 데이터 변경
    public void completeSignUp(Account account) {
        account.compltesSignUp();
        login(account);
    }
}
