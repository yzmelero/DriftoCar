/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Classe que representa una incidència registrada al sistema.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incidencia")
public class Incidencia {

    /**
     * Identificador únic de la incidència.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Estat de la incidència (1 = actiu, 0 = inactiu).
     */
    private boolean estat;

    /**
     * Motiu o descripció breu de la incidència.
     */
    private String motiu;

    /**
     * Data i hora en què la incidència va començar.
     */
    @Column(name = "data_inici_incidencia")
    private LocalDateTime dataIniciIncidencia;

    /**
     * Data i hora en què la incidència es va tancar.
     */
    @Column(name = "data_fi_incidencia")
    private LocalDateTime dataFiIncidencia;

    /**
     * Descripció detallada de la incidència.
     */
    @Column(name = "descripcio")
    private String descripcio;

    /**
     * Vehicle afectat per la incidència. Relació Many-to-One amb la classe Vehicle.
     */
    @ManyToOne
    @JoinColumn(name = "matricula", referencedColumnName = "matricula", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehicle matricula;
}
