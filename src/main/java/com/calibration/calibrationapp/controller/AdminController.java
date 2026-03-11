package com.calibration.calibrationapp.controller;

import com.calibration.calibrationapp.entity.Client;
import com.calibration.calibrationapp.entity.Appareil;
import com.calibration.calibrationapp.entity.EtalonnageSession;
import com.calibration.calibrationapp.service.ClientService;
import com.calibration.calibrationapp.service.AppareilService;
import com.calibration.calibrationapp.service.EtalonnageSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ClientService clientService;
    private final AppareilService appareilService;
    private final EtalonnageSessionService sessionService;

    public AdminController(ClientService clientService,
            AppareilService appareilService,
            EtalonnageSessionService sessionService) {
        this.clientService = clientService;
        this.appareilService = appareilService;
        this.sessionService = sessionService;
    }

    // ===============================
    // CLIENT CRUD
    // ===============================

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/clients/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PostMapping("/clients")
    public Client createClient(@RequestBody Client client) {
        return clientService.save(client);
    }

    @PutMapping("/clients/{id}")
    public Client updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        return clientService.update(id, updatedClient);
    }

    @DeleteMapping("/clients/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.delete(id);
    }

    // ===============================
    // APPAREIL CRUD
    // ===============================

    @GetMapping("/appareils")
    public List<Appareil> getAllAppareils() {
        return appareilService.getAll();
    }

    @PostMapping("/appareils")
    public Appareil createAppareil(@RequestBody Appareil appareil) {
        return appareilService.save(appareil);
    }

    @PutMapping("/appareils/{id}")
    public Appareil updateAppareil(@PathVariable Long id, @RequestBody Appareil updated) {
        return appareilService.update(id, updated);
    }

    @DeleteMapping("/appareils/{id}")
    public void deleteAppareil(@PathVariable Long id) {
        appareilService.delete(id);
    }

    // ===============================
    // SESSIONS D'ETALONNAGE
    // ===============================

    @GetMapping("/sessions")
    public List<EtalonnageSession> getAllSessions() {
        return sessionService.getAll();
    }

    @GetMapping("/sessions/client/{clientId}")
    public List<EtalonnageSession> getSessionsByClient(@PathVariable Long clientId) {
        return sessionService.getByClient(clientId);
    }

    // POST /admin/sessions — creer une session (bloque si deja existante)
    @PostMapping("/sessions")
    public EtalonnageSession createSession(
            @RequestBody EtalonnageSessionService.SessionRequest request) {
        try {
            return sessionService.createSession(request);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // PUT /admin/sessions/{id}/appareils — ajouter des appareils a une session
    // existante
    @PutMapping("/sessions/{id}/appareils")
    public EtalonnageSession addAppareils(
            @PathVariable Long id,
            @RequestBody EtalonnageSessionService.AddAppareelsRequest request) {
        try {
            return sessionService.addAppareils(id, request);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/sessions/{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionService.delete(id);
    }
}