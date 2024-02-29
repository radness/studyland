package com.studyland.modules.account;

import com.studyland.modules.study.Study;
import com.studyland.modules.tag.Tag;
import com.studyland.modules.zone.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb = true;
    private LocalDateTime emailCheckTokenGeneratedAt;

    // 비어있는 Collection 을 setting 해주는것이 좋다.
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    // email 인증 랜덤 토큰 생성
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void compltesSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now(); // 가입한 날짜
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        // 현재 시간에서 1시간 뺀거보다 이전에 만든 경우 = 보낼 수 있다.
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
    public boolean isManagerOf(Study study) {
        return study.getManagers().contains(this);
    }
}
