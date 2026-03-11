package com.calibration.calibrationapp.service;

import com.calibration.calibrationapp.entity.Appareil;
import com.calibration.calibrationapp.entity.Client;
import com.calibration.calibrationapp.entity.EtalonnageSession;
import com.calibration.calibrationapp.repository.AppareilRepository;
import com.calibration.calibrationapp.repository.ClientRepository;
import com.calibration.calibrationapp.repository.EtalonnageSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EtalonnageSessionService {

    private final EtalonnageSessionRepository sessionRepository;
    private final AppareilRepository appareilRepository;
    private final ClientRepository clientRepository;

    public EtalonnageSessionService(
            EtalonnageSessionRepository sessionRepository,
            AppareilRepository appareilRepository,
            ClientRepository clientRepository) {
        this.sessionRepository = sessionRepository;
        this.appareilRepository = appareilRepository;
        this.clientRepository = clientRepository;
    }

    // DTO créer session
    public static class SessionRequest {
        public Long clientId;
        public LocalDate dateEtalonnage;
        public List<Long> appareilIds;
    }

    // DTO ajouter appareils (nom 100% ASCII)
    // DTO ajouter appareils (ASCII)
    // See AddAppareelsRequest below

    // ===============================
    // CREER — 1 seule session par client
    // ===============================
    @Transactional
    public EtalonnageSession createSession(SessionRequest request) {

        List<EtalonnageSession> existing = sessionRepository.findByClientId(request.clientId);
        if (!existing.isEmpty()) {
            throw new RuntimeException(
                    "Ce client possede deja une session d'etalonnage. " +
                            "Utilisez PUT /admin/sessions/{id}/appareils pour ajouter des appareils.");
        }

        Client client = clientRepository.findById(request.clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouve : " + request.clientId));

        LocalDate prochaineDate = request.dateEtalonnage.plusYears(1);

        List<Appareil> appareils = appareilRepository.findAllById(request.appareilIds);
        if (appareils.isEmpty()) {
            throw new RuntimeException("Aucun appareil selectionne ou introuvable");
        }

        for (Appareil a : appareils) {
            a.setDateEtalonnage(request.dateEtalonnage);
            a.setProchaineDate(prochaineDate);
        }
        appareilRepository.saveAll(appareils);

        EtalonnageSession session = new EtalonnageSession();
        session.setClient(client);
        session.setDateEtalonnage(request.dateEtalonnage);
        session.setProchaineDate(prochaineDate);
        session.setAppareils(new ArrayList<>(appareils));

        return sessionRepository.save(session);
    }

    // ===============================
    // AJOUTER des appareils a une session existante
    // ===============================
    @Transactional
    public EtalonnageSession addAppareils(Long sessionId, AddAppareelsRequest request) {

        EtalonnageSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session non trouvee : " + sessionId));

        List<Appareil> nouveaux = appareilRepository.findAllById(request.appareilIds);
        if (nouveaux.isEmpty()) {
            throw new RuntimeException("Aucun appareil trouve");
        }

        for (Appareil a : nouveaux) {
            a.setDateEtalonnage(session.getDateEtalonnage());
            a.setProchaineDate(session.getProchaineDate());
        }
        appareilRepository.saveAll(nouveaux);

        List<Appareil> actuels = session.getAppareils();
        for (Appareil a : nouveaux) {
            boolean existe = actuels.stream().anyMatch(x -> x.getId().equals(a.getId()));
            if (!existe)
                actuels.add(a);
        }
        session.setAppareils(actuels);

        return sessionRepository.save(session);
    }

    // ===============================
    // AUTO-ATTACH : nouvel appareil cree -> date de la session du client
    // ===============================
    @Transactional
    public void attachAppareilToClientSession(Appareil appareil) {
        List<EtalonnageSession> sessions = sessionRepository.findByClientId(
                appareil.getClient().getId());
        if (sessions.isEmpty())
            return;

        EtalonnageSession session = sessions.get(0);

        appareil.setDateEtalonnage(session.getDateEtalonnage());
        appareil.setProchaineDate(session.getProchaineDate());
        appareilRepository.save(appareil);

        List<Appareil> liste = session.getAppareils();
        boolean existe = liste.stream().anyMatch(x -> x.getId().equals(appareil.getId()));
        if (!existe) {
            liste.add(appareil);
            session.setAppareils(liste);
            sessionRepository.save(session);
        }
    }

    // ===============================
    // GET
    // ===============================
    public List<EtalonnageSession> getAll() {
        return sessionRepository.findAll();
    }

    public List<EtalonnageSession> getByClient(Long clientId) {
        return sessionRepository.findByClientId(clientId);
    }

    // ===============================
    // DELETE
    // ===============================
    public void delete(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new RuntimeException("Session non trouvee : " + id);
        }
        sessionRepository.deleteById(id);
    }

    // DTO ajouter appareils (ASCII pur)
    public static class AddAppareelsRequest {
        public List<Long> appareilIds;
    }
}