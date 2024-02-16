package com.studyland.settings.validator;


import com.studyland.account.AccountRepository;
import com.studyland.domain.Account;
import com.studyland.settings.form.NicknameForm;
import com.studyland.settings.form.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if (byNickname != null) {
            errors.rejectValue("nickname", "wrong.value", "입력한 닉네임을 사용할 수 없습니다.");
        }
    }
}
