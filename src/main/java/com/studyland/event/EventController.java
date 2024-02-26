package com.studyland.event;

import com.studyland.account.CurrentUser;
import com.studyland.domain.Account;
import com.studyland.domain.Study;
import com.studyland.event.form.EventForm;
import com.studyland.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {
    
    private final StudyService studyService;

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);// 매니저 정보만 가져온다
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());
        return "event/form";
    }
}
