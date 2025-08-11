package com.oz.project.spot.service

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.spot.entity.Spot
import com.oz.project.spot.repository.SpotRepository
import org.springframework.beans.factory.annotation.Autowired

class SpotRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private SpotRepositoryService spotRepositoryService

    @Autowired
    private SpotRepository spotRepository

    def setup() {
        spotRepository.deleteAll()
    }

    def "SpotRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def spot = Spot.builder()
                .spotAddress(inputAddress)
                .spotName(name)
                .build()

        when:
        def entity = spotRepository.save(spot)
        spotRepositoryService.updateSpotAddress(entity.getId(), modifiedAddress)

        def result = spotRepository.findAll()

        then:
        result.get(0).getSpotAddress() == modifiedAddress
    }

    def "SpotRepository update - dirty checking fail"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def spot = Spot.builder()
                .spotAddress(inputAddress)
                .spotName(name)
                .build()

        when:
        def entity = spotRepository.save(spot)
        spotRepositoryService.updateSpotAddressWithoutTransaction(entity.getId(), modifiedAddress)

        def result = spotRepository.findAll()

        then:
        result.get(0).getSpotAddress() == inputAddress
    }

    def "self invocation test"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def spot = Spot.builder()
                .spotAddress(inputAddress)
                .spotName(name)
                .build()

        when:
        spotRepositoryService.bar(Arrays.asList(spot))

        then:
        def e = thrown(RuntimeException.class)
        def result = spotRepository.findAll()
        result.size() == 1 // 트랜잭션이 적용되지 않는다 (롤백 적용x)
    }
}
