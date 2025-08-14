package com.oz.project.spot.service;

import com.oz.project.spot.cache.SpotRedisTemplateService;
import com.oz.project.spot.dto.SpotDto;
import com.oz.project.spot.entity.Spot;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotSearchService {

    private final SpotRepositoryService spotRepositoryService;
    private final SpotRedisTemplateService spotRedisTemplateService;

    public List<SpotDto> searchSpotDtoList() {

        // redis
        List<SpotDto> spotDtoList = spotRedisTemplateService.findAll();
        if (!spotDtoList.isEmpty()) {
            log.info("[SpotSearchService][searchSpotDtoList] redis findAll success! - size: {}", spotDtoList.size());
            return spotDtoList;
        }

        // db
        return spotRepositoryService.findAll()
                .stream()
                .map(this::convertToSpotDto)
                .toList();
    }

    private SpotDto convertToSpotDto(Spot spot) {
        return SpotDto.builder()
                .id(spot.getId())
                .spotName(spot.getSpotName())
                .spotAddress(spot.getSpotAddress())
                .latitude(spot.getLatitude())
                .longitude(spot.getLongitude())
                .build();
    }
}
