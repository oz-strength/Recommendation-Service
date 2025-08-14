package com.oz.project.spot.service;

import com.oz.project.spot.entity.Spot;
import com.oz.project.spot.repository.SpotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotRepositoryService {

    private final SpotRepository spotRepository;

    // self invocation test
    public void bar(List<Spot> spotList) {
        log.info("bar CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        foo(spotList);
    }

    // self invocation test
    @Transactional
    public void foo(List<Spot> spotList) {
        log.info("foo CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        spotList.forEach(spot -> {
            spotRepository.save(spot);
            throw new RuntimeException("error"); // 예외발생
        });
    }

    @Transactional
    public List<Spot> saveAll(List<Spot> spotList) {
        if(spotList == null || spotList.isEmpty()) return List.of();
        return spotRepository.saveAll(spotList);
    }

    @Transactional
    public void updateSpotAddress(Long id, String address) {
        spotRepository.findById(id).ifPresentOrElse(spot -> {
            spot.changeSpotAddress(address);
        }, () -> {
            log.error("[SpotRepositoryService][updateSpotAddress] id {} not found", id);
        });
    }

    // for test
    public void updateSpotAddressWithoutTransaction(Long id, String address) {
        spotRepository.findById(id).ifPresentOrElse(spot -> {
            spot.changeSpotAddress(address);
        }, () -> {
            log.error("[SpotRepositoryService][updateSpotAddressWithoutTransaction] id {} not found", id);
        });
    }

    @Transactional(readOnly = true)
    public List<Spot> findAll() {
        return spotRepository.findAll();
    }
}
