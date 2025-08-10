package com.oz.project.gym.service;

import com.oz.project.gym.dto.GymDto;
import com.oz.project.gym.entity.Gym;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymSearchService {

    private final GymRepositoryService gymRepositoryService;

    public List<GymDto> searchGymDtoList() {

        // db
        return gymRepositoryService.findAll()
                .stream()
                .map(this::convertToGymDto)
                .toList();
    }

    private GymDto convertToGymDto(Gym gym) {
        return GymDto.builder()
                .id(gym.getId())
                .gymName(gym.getGymName())
                .gymAddress(gym.getGymAddress())
                .latitude(gym.getLatitude())
                .longitude(gym.getLongitude())
                .build();
    }
}
