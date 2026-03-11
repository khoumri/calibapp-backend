package com.calibration.calibrationapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calibration.calibrationapp.entity.*;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
