package com.studyland.modules.notification.study.event;

import com.studyland.domain.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
