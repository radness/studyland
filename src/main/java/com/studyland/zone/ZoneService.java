package com.studyland.zone;

import com.studyland.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    // Bean 이 만들어진 이후에 실행이 되는 지점
    // spring 초기 버전 제공. Bean 이 초기화 된 후 코드 블럭이 실행된다.
    @PostConstruct
    public void initZoneData() throws IOException {
        String filePath = "zones_kr.csv";
        if (zoneRepository.count() == 0) {
            // Resource -> 스프링 핵심 기술 강의에 설명이 되어있음.
            Resource resource = new ClassPathResource(filePath);
            // zones_kr 파일에 있는 데이터를 객체로 읽어온다.
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }
    }
}
