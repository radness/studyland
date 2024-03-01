package com.studyland.modules.account.validator;


import com.studyland.modules.account.AccountRepository;
import com.studyland.modules.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
// private final type의 맴버 변수의 생성자를 자동으로 만들어준다.
// private에 해당하는 맴버 변수는 제외.
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

//    @RequiredArgsConstructor 가 만들어 주는 생성자
//    따라서 해당 어노테이션이 존재하면 @Autowired 같은 어노테이션은 필요가 없다.
//    public SignUpFormValidator(AccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//    }
@Override
public boolean supports(Class<?> aClass) {
    return aClass.isAssignableFrom(SignUpForm.class);
}

    @Override
    public void validate(Object object, Errors errors) {
        SignUpForm signUpForm = (SignUpForm)object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getEmail()}, "이미 사용중인 닉네임입니다.");
        }
    }
}
