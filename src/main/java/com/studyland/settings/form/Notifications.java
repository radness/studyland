package com.studyland.settings.form;

import lombok.Data;

@Data
public class Notifications {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

//    public Notifications(Account account) {
//        // Notifications 은 Bean 이 아니기 때문에 다른 Bean 을 주입받을 수 없다.(스프링 핵심 기술)
//        // 직접 ModelMapper 를 만들어서 사용할 수 있다.
//        ModelMapper modelMapper = new ModelMapper();
//
//        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
//        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
//        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
//        this.studyEnrollmentResultByWeb = account.isStudyUpdatedByWeb();
//        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
//        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
//    }
}
