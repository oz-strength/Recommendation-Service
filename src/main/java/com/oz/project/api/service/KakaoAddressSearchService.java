package com.oz.project.api.service;

import com.oz.project.api.dto.KakaoApiResponseDto;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService kakaoUriBuilderService;

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    public Optional<KakaoApiResponseDto> requestAddressSearch(String address) {

        if(address == null || address.isBlank()) {
            log.warn("Address is null or empty");
            return Optional.empty();
        }

        try {
            URI uri = kakaoUriBuilderService.buildUriByAddressSearch(address);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            KakaoApiResponseDto response = restTemplate
                    .exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class)
                    .getBody();

            if (response != null && response.getDocumentList() != null && !response.getDocumentList().isEmpty()) {
                return Optional.of(response);
            }

            log.warn("주소 검색 결과 없음: {}", address);
        } catch (Exception e) {
            log.error("카카오 API 호출 실패: {}", address, e);
        }

        return Optional.empty();
    }
}
