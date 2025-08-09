package com.oz.project.gym.service;

import com.oz.project.gym.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymRepositoryService {

    private final GymRepository gymRepository;

    @Transactional
    public void updateGymAddress(Long id, String address) {
        gymRepository.findById(id).ifPresentOrElse(gym -> {
            gym.changeGymAddress(address);
        }, () -> {
            log.error("[GymRepositoryService][updateGymAddress] Gym with id {} not found", id);
        });
    }

    // for test
    public void updateGymAddressWithoutTransaction(Long id, String address) {
        gymRepository.findById(id).ifPresentOrElse(gym -> {
            gym.changeGymAddress(address);
        }, () -> {
            log.error("[GymRepositoryService][updateGymAddressWithoutTransaction] Gym with id {} not found", id);
        });
    }
}
