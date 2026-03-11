package com.calibration.calibrationapp.service;

import com.calibration.calibrationapp.entity.Appareil;
import com.calibration.calibrationapp.repository.AppareilRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppareilService {

    private final AppareilRepository appareilRepository;

    public AppareilService(AppareilRepository appareilRepository) {
        this.appareilRepository = appareilRepository;
    }

    // ===============================
    // GET ALL
    // ===============================
    public List<Appareil> getAll() {
        return appareilRepository.findAll();
    }

    // ===============================
    // GET BY ID
    // ===============================
    public Appareil getById(Long id) {
        return appareilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appareil avec ID " + id + " non trouvé"));
    }

    // ===============================
    // CREATE
    // ===============================
    public Appareil save(Appareil appareil) {
        return appareilRepository.save(appareil);
    }

    // ===============================
    // UPDATE
    // ===============================
    public Appareil update(Long id, Appareil updatedAppareil) {

        Appareil existing = getById(id);

        existing.setDesignation(updatedAppareil.getDesignation());
        existing.setCode(updatedAppareil.getCode());
        existing.setMarque(updatedAppareil.getMarque());
        existing.setType(updatedAppareil.getType());
        existing.setNumeroSerie(updatedAppareil.getNumeroSerie());
        existing.setNumeroCertificat(updatedAppareil.getNumeroCertificat());
        existing.setDateEtalonnage(updatedAppareil.getDateEtalonnage());
        existing.setProchaineDate(updatedAppareil.getProchaineDate());
        existing.setClient(updatedAppareil.getClient());

        return appareilRepository.save(existing);
    }

    // ===============================
    // DELETE
    // ===============================
    public void delete(Long id) {

        if (!appareilRepository.existsById(id)) {
            throw new RuntimeException("Appareil avec ID " + id + " n'existe pas");
        }

        appareilRepository.deleteById(id);
    }

    // ===============================
    // FIND BY CLIENT USERNAME
    // ===============================
    public List<Appareil> findByClientUsername(String username) {
        return appareilRepository.findByClientUserUsername(username);
    }
}
