package com.oz.project.api.service

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.api.dto.KakaoApiResponseDto
import org.springframework.beans.factory.annotation.Autowired

class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    def "address 파라미터 값이 null 이면, requestAddressSearch 메서드는 null 을 반환한다."() {
        given:
        String address = null

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        result == Optional.empty()
    }

    def "주소값이 valid 하면, requestAddressSearch 메서드는 정삭적으로 document를 반환한다."() {
        given:
        String address = "서울 서초구 반포대로 34"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address)

        then:
        result.ifPresent { res ->
            res.documentList.size() > 0
            res.metaDto.totalCount > 0
            res.documentList.get(0).addressName != null
        }
    }

    def "정상적인 주소를 입력했을 경우, 정삭정으로 위도, 경도로 변환한다."() {
        given:
        boolean actualResult = false

        when:
        def searchResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        if(searchResult.isEmpty()) actualResult = false
        else actualResult = searchResult.get().documentList.size() > 0

        where:
        inputAddress                            | expectedResult
        "서울특별시 서초구 잠원로3길 40"            | true
        "서울특별시 서초구 서초대로78길 48"          | true
        "서울특별시 서초구 반포대로 34"             | true
        "서울특별시 서초구 잘못된 주소"              | false
        "서울특별시 서초구 남부순환로356길 61"        | true
        "광진구 구의동 251-45"                    | true
        "서울특별시 강남구 테헤란로 123123123123"   | false

    }
}
