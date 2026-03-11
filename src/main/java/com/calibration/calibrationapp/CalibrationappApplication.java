package com.calibration.calibrationapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.calibration.calibrationapp.repository")
@EntityScan("com.calibration.calibrationapp.entity")
public class CalibrationappApplication {
	public static void main(String[] args) {
		SpringApplication.run(CalibrationappApplication.class, args);
	}
}
