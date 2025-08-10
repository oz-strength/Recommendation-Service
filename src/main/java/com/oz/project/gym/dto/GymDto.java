package com.oz.project.gym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymDto {

    private Long id;
    private String gymName;
    private String gymAddress;
    private double latitude;
    private double longitude;
}
