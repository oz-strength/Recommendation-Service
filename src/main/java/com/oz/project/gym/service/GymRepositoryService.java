package com.oz.project.gym.service;

import com.oz.project.gym.entity.Gym;
import com.oz.project.gym.repository.GymRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymRepositoryService {

    private final GymRepository gymRepository;

    // self invocation test
    public void bar(List<Gym> gymList) {
        log.info("bar CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        foo(gymList);
    }

    // self invocation test
    @Transactional
    public void foo(List<Gym> gymList) {
        log.info("foo CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        gymList.forEach(gym -> {
            gymRepository.save(gym);
            throw new RuntimeException("error"); // 예외발생
        });
    }

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
