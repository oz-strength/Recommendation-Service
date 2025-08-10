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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(
            retryFor = {RuntimeException.class}, // 예외 타입을 지정하여 재시도
            maxAttempts = 2, // 최대 재시도 횟수
            backoff = @Backoff(delay = 2000) // 재시도 간격
    )
    public Optional<KakaoApiResponseDto> requestAddressSearch(String address) {

        if (address == null || address.isBlank()) {
            log.warn("Address is null or empty");
            return Optional.empty();
        }

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

        return Optional.empty();
    }

    @Recover
    public Optional<KakaoApiResponseDto> recover(RuntimeException e, String address) {
        log.error("카카오 주소 검색 재시도 실패: {}", address, e);
        return Optional.empty();
    }
}
