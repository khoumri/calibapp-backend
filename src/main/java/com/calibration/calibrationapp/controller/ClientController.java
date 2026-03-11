package com.calibration.calibrationapp.controller;

import com.calibration.calibrationapp.entity.Appareil;
import com.calibration.calibrationapp.service.AppareilService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final AppareilService appareilService;

    public ClientController(AppareilService appareilService) {
        this.appareilService = appareilService;
    }

    // Voir ses appareils
    @GetMapping("/appareils")
    public List<Appareil> getMyAppareils(Authentication authentication) {

        String username = authentication.getName();

        return appareilService.findByClientUsername(username);
    }
}
