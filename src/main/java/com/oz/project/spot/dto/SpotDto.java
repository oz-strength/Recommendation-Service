package com.oz.project.spot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotDto {

    private Long id;
    private String spotName;
    private String spotAddress;
    private double latitude;
    private double longitude;
}
