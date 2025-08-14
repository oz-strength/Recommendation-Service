package com.oz.project.spot.service;

import com.oz.project.api.dto.DocumentDto;
import com.oz.project.api.dto.KakaoApiResponseDto;
import com.oz.project.api.service.KakaoAddressSearchService;
import com.oz.project.direction.dto.OutputDto;
import com.oz.project.direction.entity.Direction;
import com.oz.project.direction.service.Base62Service;
import com.oz.project.direction.service.DirectionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    private final Base62Service base62Service;

    @Value("${spot.recommendation.base.url}")
    private String baseUrl;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    public List<OutputDto> recommendSpotList(String address) {

        return kakaoAddressSearchService.requestAddressSearch(address)
                .filter(response -> !isInvalidResponse(response))
                .map(response -> {
                    DocumentDto documentDto = response.getDocumentList().getFirst();
                    // 공공기관 데이터 및 거리계산 알고리즘 이용
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
                    return List.of();
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
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId())) // shorten url
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}
