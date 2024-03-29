package com.studyland.modules.account;

import com.studyland.modules.account.form.SignUpForm;
import com.studyland.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        // attributeName을 생략하면 클래스 이름을 camelCase된 이름으로 사용된다.
//        model.addAttribute("signUpForm", new SignUpForm());
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }
    @PostMapping("/sign-up")
    // @ModelAttribute 는 파라미터로 쓰일 때 생략 가능.
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) { // SignUpForm에 정의한 JSR303 애너테이션 설정에 대해서 범위 제한에 걸리면 Error에 걸린다.
            return "account/sign-up"; // error가 존재하면 다시 form으로 간다.
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);

        return "redirect:/";
    }

    // 인증 메일 처리
    @GetMapping("/check-email-token")
    // model : 화면에 전달해야할 model
    public String checkEmailToken(String token, String email, Model model) {
        // email 해당하는 user 확인
        // repository 도메인 계층으로 보는지 or layered 계층(c,s,r,dao)으로 보느냐에 따라서ㄹ
        // repository 를 controller 쓰는가
        // 여기에서는 repository 를 domain 과 같은 level 로 본다.
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        // email 이 있는 경우
        if (!account.isValidToken(token)) { // 리팩토링
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(account);

        // view 에 전달해줘야하는 정보
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        // 해당 부분을 주석처리하면 이메일 재전송 확인이 가능함.
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account) {
        Account accountToView = accountService.getAccount(nickname);
        // attribute 에 넣어주는 객체의 이름은 무엇인가?
        // attribute name 주지 않으면 camelCase 넣어준다.
        model.addAttribute(accountToView);
        model.addAttribute("isOwner", accountToView.equals(account));
        return "account/profile";
    }

    @PostMapping("/email-login")
    public String setndEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 후에 사용할 수 있습니다.");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/email-login")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if( account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인 할 수 없습니다.");
            return view;
        }
        accountService.login(account);
        return view;
    }

}
