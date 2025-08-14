package com.oz.project.spot.cache

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.spot.dto.SpotDto
import org.springframework.beans.factory.annotation.Autowired

class SpotRedisTemplateServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private SpotRedisTemplateService spotRedisTemplateService

    def setup() {
        spotRedisTemplateService.findAll()
                .forEach(dto -> {
                    spotRedisTemplateService.delete(dto.getId())
                })
    }

    def "save success"() {
        given:
        String spotName = "testSpot"
        String spotAddress = "testAddress"
        SpotDto dto =
                SpotDto.builder()
                        .id(1L)
                        .spotName(spotName)
                        .spotAddress(spotAddress)
                        .build()

        when:
        spotRedisTemplateService.save(dto)
        def result = spotRedisTemplateService.findAll()

        then:
        result.size() == 1
        result.get(0).getId() == 1L
        result.get(0).getSpotName() == spotName
        result.get(0).getSpotAddress() == spotAddress
    }

    def "success fail"() {
        given:
        SpotDto dto =
                SpotDto.builder()
                        .build()

        // 사전 검증
        assert dto.id == null : "SpotDto id가 null이 아닙니다. 현재 값: ${dto.id}"

        when:
        spotRedisTemplateService.save(dto)
        def result = spotRedisTemplateService.findAll()

        then:
        result.size() == 0
    }

    def "delete success"() {
        given:
        String spotName = "testSpot"
        String spotAddress = "testAddress"
        SpotDto dto =
                SpotDto.builder()
                        .id(1L)
                        .spotName(spotName)
                        .spotAddress(spotAddress)
                        .build()
        spotRedisTemplateService.save(dto)

        when:
        spotRedisTemplateService.delete(dto.getId())
        def result = spotRedisTemplateService.findAll()

        then:
        result.size() == 0
    }
}
