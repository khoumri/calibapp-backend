package com.calibration.calibrationapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appareil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String designation;
    private String code;
    private String marque;
    private String type;
    private String numeroSerie;
    private String numeroCertificat;

    private LocalDate dateEtalonnage;
    private LocalDate prochaineDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

}
