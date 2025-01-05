/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa un vehicle disponible per a reserves dins del sistema.
 */
@Entity
@Data // Genera getters, setters, toString, equals i hashCode
@NoArgsConstructor // Genera el constructor buit
@AllArgsConstructor // Genera el constructor amb tots els arguments
@Table(name = "vehicle")
public class Vehicle {

    /**
     * Matrícula del vehicle, identificador únic.
     */
    @Id
    private String matricula;

    /**
     * Marca del vehicle.
     */
    private String marca;

    /**
     * Model del vehicle.
     */
    private String model;

    /**
     * Any de fabricació del vehicle.
     */
    private int any;

    /**
     * Nombre de places disponibles al vehicle.
     */
    private int places;

    /**
     * Tipus de transmissió del vehicle (manual o automàtic).
     */
    @Enumerated(EnumType.STRING)
    private TipusTransmisio transmisio;

    /**
     * Tipus de combustible del vehicle (elèctric, benzina, dièsel, híbrid).
     */
    @Enumerated(EnumType.STRING)
    private TipusCombustible combustible; // String

    /**
     * Tipus del vehicle (cotxe, moto, furgoneta, camió).
     */
    @Enumerated(EnumType.STRING)
    private TipusVehicle tipus;

    /**
     * Estat de disponibilitat del vehicle (1 = disponible, 0 = no disponible).
     */
    private boolean disponibilitat;

    /**
     * Cost per hora d'ús del vehicle.
     */
    private double costHora;

    /**
     * Quilometratge màxim permès per al vehicle.
     */
    private double kmMax;

    /**
     * Fiança requerida per llogar el vehicle.
     */
    private double fianca;

    /**
     * Localització associada al vehicle.
     */
    @ManyToOne
    @JoinColumn(name = "localitzacio", referencedColumnName = "codi_postal", nullable = false)
    private Localitzacio localitzacio;

    /**
     * Imatge del vehicle emmagatzemada com a byte array.
     */
    @Lob
    @Column(name = "imatge", columnDefinition = "MEDIUMBLOB")
    private byte[] imatge;

     /**
     * Motiu associat al vehicle (pot ser null).
     */
    @JoinColumn(nullable = true)
    private String motiu;

    /**
     * Import associat al vehicle (pot ser null).
     */
    @JoinColumn(nullable = true)
    private double importe;
}
