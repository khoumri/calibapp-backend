package com.calibration.calibrationapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtalonnageSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date de la session (choisie par l'admin)
    private LocalDate dateEtalonnage;

    // Calculée automatiquement : dateEtalonnage + 1 an
    private LocalDate prochaineDate;

    // Le client concerné
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Les appareils étalonnés dans cette session
    @ManyToMany
    @JoinTable(name = "session_appareils", joinColumns = @JoinColumn(name = "session_id"), inverseJoinColumns = @JoinColumn(name = "appareil_id"))
    private List<Appareil> appareils;
}