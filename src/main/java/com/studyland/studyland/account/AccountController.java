package com.studyland.studyland.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;

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

        // TODO 회원 가입 처리
        return "redirect:/";
    }
}
