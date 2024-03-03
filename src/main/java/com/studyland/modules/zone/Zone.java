package com.studyland.modules.zone;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"city", "province"}))
public class Zone {

    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String localNameOfCity; // local 환경에 맞는 서비스 지원(ex: 한국, 일본, 중국)
    @Column(nullable = true) // null 허용
    private String province; // 지역

    @Override
    public String toString() {
        return String.format("%s(%s)/%s", city, localNameOfCity, province);
    }
}
