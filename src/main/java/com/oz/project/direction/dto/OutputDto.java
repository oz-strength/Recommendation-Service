package com.oz.project.direction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {

    private String spotName; // 관광지 이름
    private String spotAddress; // 관광지 주소
    private String directionUrl; // 길찾기 URL
    private String roadViewUrl; // 로드뷰 URL
    private String distance; // 고객 주소와 관광지 사이 거리
}
