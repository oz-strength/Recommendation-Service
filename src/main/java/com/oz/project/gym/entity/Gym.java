package com.oz.project.gym.entity;

import com.oz.project.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "gym")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gym extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gymName;
    private String gymAddress;
    private double latitude;
    private double longitude;

    public void changeGymAddress(String gymAddress) {
        this.gymAddress = gymAddress;
    }
}
