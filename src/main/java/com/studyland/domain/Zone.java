package com.studyland.domain;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
