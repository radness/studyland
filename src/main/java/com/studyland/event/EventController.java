package com.studyland.event;

import com.studyland.account.CurrentUser;
import com.studyland.domain.Account;
import com.studyland.domain.Event;
import com.studyland.domain.Study;
import com.studyland.event.form.EventForm;
import com.studyland.event.validator.EventValidator;
import com.studyland.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {
    
    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);// 매니저 정보만 가져온다
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            // eventForm 에 들어왔던 데이터와 binding, validation 할 때 발생하는 에러는
            // 기본으로 model 에 담겨진다.
            return "event/form";
        }

        // 데이터를 변경하는 작업은 서비스에 위임하겠다.
        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }
}
