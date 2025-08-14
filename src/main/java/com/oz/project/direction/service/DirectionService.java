package com.oz.project.direction.service;

import com.oz.project.api.dto.DocumentDto;
import com.oz.project.api.dto.KakaoApiResponseDto;
import com.oz.project.api.service.KakaoCategorySearchService;
import com.oz.project.direction.entity.Direction;
import com.oz.project.direction.repository.DirectionRepository;
import com.oz.project.spot.cache.TouristSpotRedisService;
import com.oz.project.spot.service.SpotSearchService;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3; // 헬스장 최대 검색 개수
    private static final double RADIUS_KM = 10.0; // 반경 10km
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";

    private final SpotSearchService spotSearchService;
    private final DirectionRepository directionRepository;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final Base62Service base62Service;
    private final TouristSpotRedisService touristSpotRedisService;


    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) {
            return List.of();
        }
        return directionRepository.saveAll(directionList);
    }

    public String findDirectionUrlById(String encodedId) {
        Long decodedId = base62Service.decodeDirectionId(encodedId);
        Direction direction = directionRepository.findById(decodedId).orElse(null);

        assert direction != null;
        String params = String.join(", ", direction.getTargetSpotName(),
                String.valueOf(direction.getTargetLatitude()), String.valueOf(direction.getTargetLongitude()));

        return UriComponentsBuilder.fromUriString(DIRECTION_BASE_URL + params).toUriString();
    }


    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) {
            return List.of();
        }

        return spotSearchService.searchSpotDtoList()
                .stream().map(spotDto ->
                        Direction.builder()
                                .inputAddress(documentDto.getAddressName())
                                .inputLatitude(documentDto.getLatitude())
                                .inputLongitude(documentDto.getLongitude())
                                .targetSpotName(spotDto.getSpotName())
                                .targetAddress(spotDto.getSpotAddress())
                                .targetLatitude(spotDto.getLatitude())
                                .targetLongitude(spotDto.getLongitude())
                                .distance(
                                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                                spotDto.getLatitude(), spotDto.getLongitude())
                                ).build())
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .toList();
    }

    // spot search by category kakao api
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {

        if (Objects.isNull(inputDocumentDto)) {
            return List.of();
        }

        double lat = inputDocumentDto.getLatitude();
        double lng = inputDocumentDto.getLongitude();

        // 1. 캐시 조회
        Optional<KakaoApiResponseDto> cached = touristSpotRedisService.getCachedSpots(lat, lng, RADIUS_KM);
        KakaoApiResponseDto apiResponse;

        if (cached.isPresent()) {
            log.info("[Cache Hit] Tourist spots for lat={}, lng={}", lat, lng);
            apiResponse = cached.get();
        } else {
            // 2. API 호출
            apiResponse = kakaoCategorySearchService.requestTouristSpotCategorySearch(lat, lng, RADIUS_KM);

            // 3. 캐시 저장
            touristSpotRedisService.saveSpotsCache(lat, lng, RADIUS_KM, apiResponse, Duration.ofMinutes(10));
        }

        // 4. Direction 변환
        return apiResponse.getDocumentList().stream()
                .map(result -> Direction.builder()
                        .inputAddress(inputDocumentDto.getAddressName())
                        .inputLatitude(lat)
                        .inputLongitude(lng)
                        .targetSpotName(result.getPlaceName())
                        .targetAddress(result.getAddressName())
                        .targetLatitude(result.getLatitude())
                        .targetLongitude(result.getLongitude())
                        .distance(result.getDistance() * 0.001)
                        .build())
                .limit(MAX_SEARCH_COUNT)
                .toList();

    }

    // Haversine formula
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        return R * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
    }
}
