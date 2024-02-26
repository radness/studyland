package com.studyland.domain;

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
    
    private boolean attend; // 참석 여부

}
