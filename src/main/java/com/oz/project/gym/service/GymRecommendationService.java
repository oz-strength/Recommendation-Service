package com.oz.project.gym.service;

import com.oz.project.api.dto.DocumentDto;
import com.oz.project.api.dto.KakaoApiResponseDto;
import com.oz.project.api.service.KakaoAddressSearchService;
import com.oz.project.direction.entity.Direction;
import com.oz.project.direction.service.DirectionService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    public void recommendGymList(String address) {

        Optional<KakaoApiResponseDto> kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (isInvalidResponse(kakaoApiResponseDto)) {
            log.error("[GymRecommendationService][recommendGymList] - fail >> Input address: {}", address);
            return;
        }

        kakaoApiResponseDto.ifPresent(response -> {
            DocumentDto documentDto = response.getDocumentList().getFirst();
            List<Direction> directionList = directionService.buildDirectionList(documentDto);
            directionService.saveAll(directionList);
        });

    }

    private boolean isInvalidResponse(Optional<KakaoApiResponseDto> response) {
        return response.map(dto -> dto.getDocumentList() == null || dto.getDocumentList().isEmpty())
                .orElse(true);
    }
}
