package com.studyland.modules.study;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    // QuerydslRepositorySupport 를 상속받으면 compile 에러가 발생한다.
    // -> repository의 인스턴스를 만들려고 하면 상위 클래스의 생성자를 호출한다.
    // 기본생성자만 존재하므로 기본생성자를 호출하려고 한다.
    // 상위 클래스에 기본생성자가 없으므로 에러가 발생.
    // 상위 클래스에 default 생성자가 없고 파라미터를 하나 받는 생성자만 존재하므로 StudyRepositoryExtensionImpl 인스턴스를 만드려면
    // 자식클래스의 생성자를 만들어줘야한다.
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {
        // queryDsl 사용
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)));

        return query.fetch(); // 쿼리 실행
        // query.fetchResults() : 페이징 처리 할 때 사용
    }
}
