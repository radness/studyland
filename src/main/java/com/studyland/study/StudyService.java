package com.studyland.study;

import com.studyland.domain.Account;
import com.studyland.domain.Study;
import com.studyland.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyToUpdate(Account account, String path) throws AccessDeniedException {
        Study study = this.getStudy(path);
        // 권한이 있는 사용자인지 체크하고 가져온다.
        checkIfManager(account, study);
        return study;
    }

    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    private void checkIfManager(Account account, Study study) throws AccessDeniedException {
        if (!study.isManagedBy(account)) {
            // AccessDeniedException : spring security
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
    }
}
