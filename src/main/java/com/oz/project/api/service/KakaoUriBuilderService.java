package com.oz.project.api.service;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    private static final String KAKAO_LOCAL_SEARCH_CATEGORY_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    public URI buildUriByAddressSearch(String address) {

        URI uri = URI.create(KAKAO_LOCAL_SEARCH_ADDRESS_URL);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri)
                .queryParam("query", address);

        log.info("KakaoUriBuilderService.buildUriByAddressSearch URI: {}", uri);
        log.info("KakaoUriBuilderService.buildUriByAddressSearch() - address: {}, encodedUri: {}", address, uriBuilder.build().encode().toUri());
        return uriBuilder.build().encode().toUri();
    }

    public URI buildUriByCategorySearch(double latitude, double longitude, double radius, String category) {

        double meterRadius = radius * 1000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(KAKAO_LOCAL_SEARCH_CATEGORY_URL)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", meterRadius)
                .queryParam("category_group_code", category);

        URI uri = uriBuilder.build().encode().toUri();

        log.info("[KakaoUriBuilderService][buildUriByCategorySearch] URI: {}", uri);

        return uri;
    }
}
