package com.studyland.modules.notification;

import com.studyland.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account; // 알림

    private LocalDateTime createdLocalDateTime;

    // 자바 enum 타입을 엔티티 클래스의 속성으로 사용할 수 있다.
    // @Enumerated 2가지 enumType 이 존재
    // EnumType.STRING : enum 이름을 db에 저장
    // EnumType.ORIGINAL : enum 순서 값을 db에 저장
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
