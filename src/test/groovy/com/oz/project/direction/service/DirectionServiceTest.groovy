package com.oz.project.direction.service

import com.oz.project.api.dto.DocumentDto
import com.oz.project.direction.repository.DirectionRepository
import com.oz.project.gym.dto.GymDto
import com.oz.project.gym.service.GymSearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    private GymSearchService gymSearchService = Mock()
    private DirectionRepository directionRepository = Mock()

    private DirectionService directionService = new DirectionService(gymSearchService, directionRepository)

    private List<GymDto> gymList

    def setup() {
        gymList = new ArrayList<>()
        gymList.addAll(
                GymDto.builder()
                        .id(1L)
                        .gymName("MKG 피트니스")
                        .gymAddress("주소1")
                        .latitude(37.4860057231492)
                        .longitude(127.010107986769)
                        .build(),
                GymDto.builder()
                        .id(2L)
                        .gymName("휘트니스 모션")
                        .gymAddress("주소2")
                        .latitude(37.4915088027577)
                        .longitude(126.990928299196)
                        .build()
        )
    }

    def "buildDirectionList - 결과 값이 거리 순으로 정렬이 되는지 확인"() {
        given:
        def addressName = "서울 서초구 서초대로 103" // 내방역
        double inputLatitude = 37.487533528504
        double inputLongitude = 126.993190309509

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:
        gymSearchService.searchGymDtoList() >> gymList // stub gymSearchService to return gymList

        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetGymName == "휘트니스 모션"
        results.get(1).targetGymName == "MKG 피트니스"

    }

    def "buildDirectionList - 정해진 반경 10km 내에 검색되는지 확인"() {
        // 10km 반경 외에 있는 헬스장
        // 경기 광명시 오리로 857 지하1층 짐박스피트니스
        // "x": "126.866261238015",
        // "y": "37.4748973430136"
        given:
        gymList.add(
                GymDto.builder()
                        .id(3L)
                        .gymName("짐박스피트니스")
                        .gymAddress("주소2")
                        .latitude(37.4748973430136)
                        .longitude(126.866261238015)
                        .build())

        def addressName = "서울 서초구 서초대로 103" // 내방역
        double inputLatitude = 37.487533528504
        double inputLongitude = 126.993190309509

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()
        when:
        gymSearchService.searchGymDtoList() >> gymList // stub gymSearchService to return gymList

        def results = directionService.buildDirectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetGymName == "휘트니스 모션"
        results.get(1).targetGymName == "MKG 피트니스"
    }
}
