package com.oz.project.gym.repository

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.gym.entity.Gym
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.LocalDateTime

class GymRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private GymRepository gymRepository

    def setup() {
        gymRepository.deleteAll()
    }

    def "GymRepository save"() {
        given:
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"
        double longitude = 127.015892124665
        double latitude = 37.4940331549084

        def gym = Gym.builder()
                .gymAddress(address)
                .gymName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()


        when:
        def result = gymRepository.save(gym)

        then:
        result.getGymAddress() == address
        result.getGymName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude
    }

    def "GymRepository saveAll"() {
        given:
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"
        double longitude = 127.015892124665
        double latitude = 37.4940331549084

        def gym = Gym.builder()
                .gymAddress(address)
                .gymName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        gymRepository.saveAll(Arrays.asList(gym))
        def result = gymRepository.findAll()

        then:
        result.size() == 1
    }

    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 서초구 서초동 1674-1"
        String name = "휘트니스에프엠 교대점"

        def gym = Gym.builder()
                .gymAddress(address)
                .gymName(name)
                .build()

        when:
        gymRepository.save(gym)
        def result = gymRepository.findAll()

        then:
        result.get(0).getCreatedDate().isAfter(now)
        result.get(0).getModifiedDate().isAfter(now)
    }
}
