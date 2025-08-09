package com.oz.project.gym.repository;

import com.oz.project.gym.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<Gym, Long> {

}
