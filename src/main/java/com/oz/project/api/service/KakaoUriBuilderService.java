package com.oz.project.api.service;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public URI buildUriByAddressSearch(String address) {

        URI uri = URI.create(KAKAO_LOCAL_SEARCH_ADDRESS_URL);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri)
                .queryParam("query", address);

        log.info("KakaoUriBuilderService.buildUriByAddressSearch URI: {}", uri);
        log.info("KakaoUriBuilderService.buildUriByAddressSearch() - address: {}, encodedUri: {}", address, uriBuilder.build().encode().toUri());
        return uriBuilder.build().encode().toUri();

    }
}
