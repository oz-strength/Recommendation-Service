package com.oz.project.api.service

import com.oz.project.AbstractIntegrationContainerBaseTest
import com.oz.project.api.dto.DocumentDto
import com.oz.project.api.dto.KakaoApiResponseDto
import com.oz.project.api.dto.MetaDto
import com.oz.project.spot.cache.AddressSearchRedisService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

class KakaoAddressSearchServiceRetryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    @SpringBean
    private AddressSearchRedisService addressSearchRedisService = Mock()

    @SpringBean
    private KakaoUriBuilderService kakaoUriBuilderService = Mock()

    private MockWebServer mockWebServer

    private ObjectMapper objectMapper = new ObjectMapper()

    private String inputAddress = "서울 서초구 서초대로 103" // 내방역

    def setup() {
        mockWebServer = new MockWebServer()
        mockWebServer.start()
        addressSearchRedisService.getCachedAddress(inputAddress) >> Optional.empty()
        println mockWebServer.port
        println mockWebServer.url("/")
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "requestAddressSearch retry success"() {
        given:
        def metaDto = new MetaDto(1)
        def documentDto = DocumentDto.builder()
                .addressName(inputAddress)
                .build()
        def expectedResponse = new KakaoApiResponseDto(metaDto, Arrays.asList(documentDto))
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(expectedResponse)))

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        kakaoApiResult.ifPresent {
            result ->
            assert result.getDocumentList().size() == 1
            assert result.getMetaDto().totalCount == 1
            assert result.getDocumentList().get(0).getAddressName() == inputAddress
        }
    }

    def "requestAddressSearch retry failure"() {
        given:
        def uri = mockWebServer.url("/").uri()

        when:
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))
        mockWebServer.enqueue(new MockResponse().setResponseCode(504))

        def kakaoApiResult = kakaoAddressSearchService.requestAddressSearch(inputAddress)

        then:
        2 * kakaoUriBuilderService.buildUriByAddressSearch(inputAddress) >> uri
        !kakaoApiResult.isPresent()
    }
}
