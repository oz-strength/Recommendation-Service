package com.oz.project.spot.service

import com.oz.project.spot.cache.SpotRedisTemplateService
import com.oz.project.spot.entity.Spot
import org.assertj.core.util.Lists
import spock.lang.Specification

class SpotSearchServiceTest extends Specification {

    private SpotSearchService spotSearchService

    private SpotRepositoryService spotRepositoryService = Mock()
    private SpotRedisTemplateService spotRedisTemplateService = Mock()

    private List<Spot> spotList

    def setup() {

        spotSearchService = new SpotSearchService(spotRepositoryService, spotRedisTemplateService)

        spotList = Lists.newArrayList(
                Spot.builder()
                .id(1L)
                .spotName("시흥계곡")
                .latitude(37.4455447405375)
                .longitude(126.911510533536)
                .build(),
                Spot.builder()
                .id(2L)
                .spotName("호암산나들길")
                .latitude(37.446007130772685)
                .longitude(126.92336229635185)
                .build()
        )
    }

    def "레디스 장애 시 DB를 이용하여 관광지 데이터 조회"() {
        when:
        spotRedisTemplateService.findAll() >> []
        spotRepositoryService.findAll() >> spotList

        def result = spotSearchService.searchSpotDtoList()

        then:
        result.size() == 2
        result[0].id == 1L
        result[0].spotName == "시흥계곡"
        result[1].id == 2L
        result[1].spotName == "호암산나들길"
    }


}
