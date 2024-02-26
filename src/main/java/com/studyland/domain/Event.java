package com.studyland.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createBy;

    @Column(nullable = false)
    private String title;

    // 기본 전략이 LAZY라면 description은 가져오면 안된다.
    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    private int limitOfEnrollments;

    // 양방향 관계(한쪽이 주인이 되어야 한다.)
    // event 객체에 있는 enrollments 컬렉션 정보를 변경해도 db에는 반영이 되지 않는다.
    // 연관 관계 매핑은 Enrollment 객에츼 event에서 @ManyToOne으로 한다.
    // foreign key 는 Enrollment 테이블에 생성된다.
    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    // enum 을 매핑할때는 @Enumerated를 사용
    // 기본 EnumType은 ORDINAL(순서) 값이 들어간다.
    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
