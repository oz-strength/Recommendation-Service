package com.oz.project.spot.entity;

import com.oz.project.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "spot")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotName;
    private String spotAddress;
    private double latitude;
    private double longitude;

    public void changeSpotAddress(String spotAddress) {
        this.spotAddress = spotAddress;
    }
}
