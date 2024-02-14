package com.studyland.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; // 인증된 계정인지

    private String emailCheckToken; // 토큰값

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location; // 거주 지역

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb;

    // email 인증 랜덤 토큰 생성
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public void compltesSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now(); // 가입한 날짜
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }
}
