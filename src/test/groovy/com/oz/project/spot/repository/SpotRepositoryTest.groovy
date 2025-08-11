package com.oz.project.spot.repository

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.spot.entity.Spot
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

class SpotRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private SpotRepository spotRepository

    def setup() {
        spotRepository.deleteAll()
    }

    def 'spotRepository save'() {
        given:
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"
        double longitude = 127.015892124665
        double latitude = 37.4940331549084

        def spot = Spot.builder()
                .spotAddress(address)
                .spotName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()


        when:
        def result = spotRepository.save(spot)

        then:
        result.getSpotAddress() == address
        result.getSpotName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude
    }

    def 'SpotRepository saveAll'() {
        given:
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"
        double longitude = 127.015892124665
        double latitude = 37.4940331549084

        def spot = Spot.builder()
                .spotAddress(address)
                .spotName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        spotRepository.saveAll(Arrays.asList(spot))
        def result = spotRepository.findAll()

        then:
        result.size() == 1
    }

    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"

        def spot = Spot.builder()
                .spotAddress(address)
                .spotName(name)
                .build()

        when:
        spotRepository.save(spot)
        def result = spotRepository.findAll()

        then:
        result.get(0).getCreatedDate().isAfter(now)
        result.get(0).getModifiedDate().isAfter(now)
    }
}
