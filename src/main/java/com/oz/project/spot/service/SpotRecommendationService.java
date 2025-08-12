package com.oz.project.spot.service;

import static java.util.stream.Collectors.toList;

import com.oz.project.api.dto.DocumentDto;
import com.oz.project.api.dto.KakaoApiResponseDto;
import com.oz.project.api.service.KakaoAddressSearchService;
import com.oz.project.direction.dto.OutputDto;
import com.oz.project.direction.entity.Direction;
import com.oz.project.direction.service.DirectionService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    public List<OutputDto> recommendSpotList(String address) {

        return kakaoAddressSearchService.requestAddressSearch(address)
                .filter(response -> !isInvalidResponse(response))
                .map(response -> {
                    DocumentDto documentDto = response.getDocumentList().getFirst();
                    // 공공기관 헬스장 데이터 및 거리계산 알고리즘 이용
                    // List<Direction> directionList = directionService.buildDirectionList(documentDto);

                    // kakao 카테고리를 이용한 장소 검색 api 이용
                    List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);
                    return directionService.saveAll(directionList)
                            .stream()
                            .map(this::convertToOutputDto)
                            .toList();
                })
                .orElseGet(() -> {
                    log.error("[SpotRecommendationService][recommendSpotList] - fail >> Input address: {}", address);
                    return Collections.emptyList();
                });
    }

    private boolean isInvalidResponse(KakaoApiResponseDto response) {
        return response == null
                || response.getDocumentList() == null
                || response.getDocumentList().isEmpty();
    }

    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder()
                .spotName(direction.getTargetSpotName())
                .spotAddress(direction.getTargetAddress())
                .directionUrl("todo") // todo
                .roadViewUrl("todo")
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}
