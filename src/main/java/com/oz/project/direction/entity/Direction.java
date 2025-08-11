package com.oz.project.direction.entity;

import com.oz.project.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "direction")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Direction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 고객
    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    // 헬스장
    private String targetSpotName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    // 고객 주소와 헬스장 주소 사이의 거리
    private double distance;
}
