package com.oz.project.spot.controller;

import com.oz.project.spot.cache.SpotRedisTemplateService;
import com.oz.project.spot.dto.SpotDto;
import com.oz.project.spot.service.SpotRepositoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SpotController {

    private final SpotRepositoryService spotRepositoryService;
    private final SpotRedisTemplateService spotRedisTemplateService;

    // 데이터 초기 세팅을 위한 임시 메서드
    @GetMapping("/redis/save")
    public String save() {

        List<SpotDto> spotDtoList = spotRepositoryService.findAll().stream()
                .map(spot -> SpotDto.builder()
                        .id(spot.getId())
                        .spotName(spot.getSpotName())
                        .spotAddress(spot.getSpotAddress())
                        .latitude(spot.getLatitude())
                        .longitude(spot.getLongitude())
                        .build()).toList();

        spotDtoList.forEach(spotRedisTemplateService::save);
        return "success";
    }
}
