package com.studyland.settings;

import com.studyland.account.CurrentUser;
import com.studyland.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController {

    // model : view 를 보여줄 때사용하는 객체
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        // form 에서 사용할 정보
        model.addAttribute(new Profile(account));
        // 생략 가능하다. 생략하면 viewName translator 가 추측해서 이름에 해당하는 값을 넣어준다.
        return "settings/profile";
    }
}
