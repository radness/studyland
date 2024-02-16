package com.studyland.account;

import com.studyland.domain.Account;
import com.studyland.domain.Tag;
import com.studyland.settings.form.Notifications;
import com.studyland.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

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
                .studyUpdatedByWeb(true)
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

    public void updateProfile(Account account, Profile profile) {
        /* account 객체는 persist 상태가 아니다. (detached 객체)
        * detached 객체는 transaction 이 종료되어도 db에 반영을 하지 않는다.
        * -> db에 sync 를 맞추는 법 : accountRepository.save 를 호출하면
        * save 구현체 안에서 id 값이 존재하면 merge 한다.(기존 데이터에 update 해준다.) */
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword)); // passwordEncoder 로 암호화 처리를 반드시해줘야함.
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        // detached 객체여서 save 를 별도로 해줘야한다.
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account); // login 을 별도로 해주지 않으면 nav bar 에 있는 nickname 정보가 갱신되지 않는다.
    }

    // 로그인 링크를 보낸다.
    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디랜드, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void addTag(Account account, Tag tag) {
        // add 할 때 주의할 점 : account 는 detached 객체
        // account 를 먼저 loading 해준다.
        Optional<Account> byId = accountRepository.findById(account.getId());
        // byId가 있으면 account 에 tag를 추가한다.
        // 없으면 아무일도 일어나지 않는다.
        byId.ifPresent(a -> a.getTags().add(tag));
        // getOne 은 lazyLoading이다. 필요한 순간에만 가져온다. EntityManager를 통해서
        // TODO lazyLoading study 필요
//        accountRepository.getOne()
    }
}
