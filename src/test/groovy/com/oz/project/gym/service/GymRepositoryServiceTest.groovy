package com.oz.project.gym.service

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.gym.entity.Gym
import com.oz.project.gym.repository.GymRepository
import org.springframework.beans.factory.annotation.Autowired

class GymRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private GymRepositoryService gymRepositoryService

    @Autowired
    private GymRepository gymRepository

    def setup() {
        gymRepository.deleteAll()
    }

    def "GymRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def gym = Gym.builder()
                .gymAddress(inputAddress)
                .gymName(name)
                .build()

        when:
        def entity = gymRepository.save(gym)
        gymRepositoryService.updateGymAddress(entity.getId(), modifiedAddress)

        def result = gymRepository.findAll()

        then:
        result.get(0).getGymAddress() == modifiedAddress
    }

    def "GymRepository update - dirty checking fail"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def gym = Gym.builder()
                .gymAddress(inputAddress)
                .gymName(name)
                .build()

        when:
        def entity = gymRepository.save(gym)
        gymRepositoryService.updateGymAddressWithoutTransaction(entity.getId(), modifiedAddress)

        def result = gymRepository.findAll()

        then:
        result.get(0).getGymAddress() == inputAddress
    }

    def "self invocation test"() {
        given:
        String inputAddress = "서울 서초구 서초동 1674-1"
        String modifiedAddress = "서울 금천구 시흥대로"
        String name = "휘트니스에프엠 교대점"

        def gym = Gym.builder()
                .gymAddress(inputAddress)
                .gymName(name)
                .build()

        when:
        gymRepositoryService.bar(Arrays.asList(gym))

        then:
        def e = thrown(RuntimeException.class)
        def result = gymRepository.findAll()
        result.size() == 1 // 트랜잭션이 적용되지 않는다 (롤백 적용x)
    }
}
