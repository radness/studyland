package com.studyland.domain;

import com.studyland.account.UserAccount;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members"),
})
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

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
        return this.managers.contains(userAccount.getAccount());
    }

    public void addMemeber(Account account) {
        this.members.add(account);
    }

    public boolean isManagedBy(Account account) {
        return this.getManagers().contains(account);
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

}

