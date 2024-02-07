package com.studyland.studyland.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        // attributeName을 생략하면 클래스 이름을 camelCase된 이름으로 사용된다.
//        model.addAttribute("signUpForm", new SignUpForm());
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

}
