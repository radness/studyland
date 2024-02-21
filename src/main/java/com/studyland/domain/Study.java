package com.studyland.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {
    @Id @GeneratedValue
    private Long id;
    @ManyToMany
    private Set<Account> managers = new HashSet<>(); // 관리 매니저
    @ManyToMany
    private Set<Account> members = new HashSet<>();
    @Column(unique = true)
    private String path;
    private String title;
    private String shortDescription;
    // EAGER : 스터디 정보를 조회할 때 무조건 가져온다.
    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription; // 본문
    @Lob @Basic(fetch = FetchType.EAGER)
    private  String image;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @ManyToMany
    private Set<Zone> zones= new HashSet<>();
    private LocalDateTime publishedDateTime;
    private LocalDateTime closedDateTime;
    private LocalDateTime recruitingUpdatedDateTime;
    private boolean recruiting; // 인원 모집 중인지
    private boolean published;
    private boolean closed;
    private boolean useBanner;

    public void addManager(Account account) {
        this.managers.add(account);
    }
}

