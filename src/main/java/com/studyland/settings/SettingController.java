package com.studyland.settings;

import com.studyland.account.AccountService;
import com.studyland.account.CurrentUser;
import com.studyland.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    private static final String SETTINGS_PROFILE_URL = "/settings/profile";

    // @RequiredArgsConstructor 를 선언하여 생성자 주입을 코드없이 자동으로 설정
    private final AccountService accountService;

    // model : view 를 보여줄 때사용하는 객체
    @GetMapping(SETTINGS_PROFILE_VIEW_NAME)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        // form 에서 사용할 정보
        model.addAttribute(new Profile(account));
        // 생략 가능하다. 생략하면 viewName translator 가 추측해서 이름에 해당하는 값을 넣어준다.
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    // ModelAttribute 객체의 Binding 을 받아주는 Error(BindingResult) 객체를 쌍으로 선언
    @PostMapping(SETTINGS_PROFILE_VIEW_NAME)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors
            , Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        // spring mvc에서 제공해주는 기능
        // 1회성으로 사용하는 데이터. model 에서 한번 사용된 후 사라진다
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
