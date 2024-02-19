package com.studyland.settings.form;

import com.studyland.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    // ex:  Seoul(서울)/None
    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    // 잘라서 파싱
    public Zone getZone() {
        return Zone.builder().city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName()).build();
    }

}
