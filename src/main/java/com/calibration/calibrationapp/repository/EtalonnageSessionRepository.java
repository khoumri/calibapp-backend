package com.calibration.calibrationapp.repository;

import com.calibration.calibrationapp.entity.EtalonnageSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtalonnageSessionRepository extends JpaRepository<EtalonnageSession, Long> {

    // Toutes les sessions d'un client
    List<EtalonnageSession> findByClientId(Long clientId);
}