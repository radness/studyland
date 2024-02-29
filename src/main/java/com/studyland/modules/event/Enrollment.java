package com.studyland.modules.event;

import com.studyland.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

    // 참석여부
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;
    
    @ManyToOne
    private Account account;
    
    private LocalDateTime enrolledAt; // 등록 순서
    
    private boolean accepted; // 수략 여부
    
    private boolean attended; // 참석 여부

}
