package com.calibration.calibrationapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calibration.calibrationapp.entity.*;

@Repository
public interface AppareilRepository extends JpaRepository<Appareil, Long> {

    List<Appareil> findByClientUserUsername(String username);
}