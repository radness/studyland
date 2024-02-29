package com.studyland.modules.account;

import com.studyland.infra.config.AppProperties;
import com.studyland.modules.account.form.SignUpForm;
import com.studyland.modules.account.form.Notifications;
import com.studyland.modules.account.form.Profile;
import com.studyland.modules.tag.Tag;
import com.studyland.modules.zone.Zone;
import com.studyland.infra.mail.EmailMessage;
import com.studyland.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j // 로깅
@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine; // 타임리프 템플릿 엔진
    private final AppProperties appProperties;

    /* sendSignUpConfirmEmail 에서 RuntimeException 이 발생하게 되면 Transection은 rollback 된다.
    * 즉 새로운 newAccount는 저장되지 않는다. */
    public Account processNewAccount(SignUpForm signUpForm) {
        // 리팩토링 : ctrl + alt + m
        Account newAccount = saveNewAccount(signUpForm); // 새로운 계정 저장
        // email 보내기
//        newAccount.generateEmailCheckToken(); // 토큰 생성
        sendSignUpConfirmEmail(newAccount); // 확인 이메일 보내기
        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        // 암호 인코딩
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        // email, nickname 등 기본값을 생성
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
//        Account account = Account.builder()
//                .email(signUpForm.getEmail())
//                .nickname(signUpForm.getNickname())
////                .password(signUpForm.getPassword())
//                .password(passwordEncoder.encode(signUpForm.getPassword())) // Password 인코딩
//                .studyCreatedByWeb(true)
//                .studyEnrollmentResultByWeb(true)
//                .studyUpdatedByWeb(true)
//                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디랜드 서비스를 사용하려면 링크를 클릭하세요.");
        // application.properties에 설정된 값으로 host value를 넣어준다.
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디랜드, 회원 가입 인증")
                .message(message) // html message
                .build();
        emailService.sendEmail(emailMessage);

//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(newAccount.getEmail());
//        simpleMailMessage.setSubject("스터디랜드, 회원 가입 인증"); // 제목
//        // 본문, 만들어보낸 토큰값을 가져와서 매개변수로 전달.
//        simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
//                + "&email=" + newAccount.getEmail());
//        javaMailSender.send(simpleMailMessage);
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
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "스터디랜드 로그인하기");
        context.setVariable("message", "로그인하려면 링크를 클릭하세요.");
        // application.properties에 설정된 값으로 host value를 넣어준다.
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("스터디랜드, 회원 가입 인증")
                .message(message) // html message
                .build();
        emailService.sendEmail(emailMessage);
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

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        // 없으면 에러를 던지고 있으면 tag 정보를 return
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().remove(zone));
    }

    public Account getAccount(String nickname) {
        Account account = accountRepository.findByNickname(nickname);
        if (account == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }
}
