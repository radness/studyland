package com.studyland.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank // 비어 있는 값이면 안된다.
    @Length(min = 3, max = 20)
    // 패턴 검사 regexp : 정규식 ㄱ~ㅎ, 가~힣, a~z, 0~9, "_" , "-" 값을 지원, 값을 3개부터 20개까지의 캐릭터가 3개에서 20개까지 들어올 수 있다.
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

}
