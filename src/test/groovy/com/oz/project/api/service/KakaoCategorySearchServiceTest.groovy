package com.oz.project.api.service

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.api.dto.DocumentDto
import com.oz.project.api.dto.KakaoApiResponseDto
import com.oz.project.api.dto.MetaDto
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.RestTemplate

class KakaoCategorySearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    KakaoCategorySearchService kakaoCategorySearchService

    @SpringBean
    RestTemplate restTemplate = Mock()

    @SpringBean
    KakaoUriBuilderService kakaoUriBuilderService = Mock()

    def "카카오 카테고리 검색 서비스 테스트"() {
        given:
        def latitude = 37.497913
        def longitude = 127.027621
        def radius = 2000
        def mockResponse = new KakaoApiResponseDto(
            documentList: [new DocumentDto(
                    placeName: "창업가거리",
                    addressName: "서울 강남구 역삼동 806",
                    latitude: latitude,
                    longitude: longitude,
                    distance: 683,
            )],
            metaDto: new MetaDto(totalCount: 1)
        )
        ReflectionTestUtils.setField(kakaoCategorySearchService, "kakaoApiKey", "test-api-key")

        when:
        def responseEntity = kakaoCategorySearchService.requestTouristSpotCategorySearch(latitude, longitude, radius)

        then:
        1 * restTemplate.exchange(_, HttpMethod.GET, { HttpEntity entity ->
            entity.headers.getFirst("Authorization") == "KakaoAK test-api-key"
        }, KakaoApiResponseDto.class) >> ResponseEntity.ok(mockResponse)
        responseEntity.documentList.size() == 1
        responseEntity.documentList[0].placeName == "창업가거리"
    }
}
