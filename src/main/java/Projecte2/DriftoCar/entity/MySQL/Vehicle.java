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
 *
 * @author Anna
 */
@Entity
@Data // Genera getters, setters, toString, equals i hashCode
@NoArgsConstructor // Genera el constructor buit
@AllArgsConstructor // Genera el constructor amb tots els arguments
@Table(name = "vehicle")
public class Vehicle {

    @Id
    private String matricula;

    private String marca;

    private String model;

    private int any;

    private int places;

    @Enumerated(EnumType.STRING)
    private TipusTransmisio transmisio;

    @Enumerated(EnumType.STRING)
    private TipusCombustible combustible; // String

    @Enumerated(EnumType.STRING)
    private TipusVehicle tipus;

    // 1 = disponible, 0 = no disponible
    private boolean disponibilitat;

    private double costHora;

    private double kmMax;

    private double fianca;

    @ManyToOne
    @JoinColumn(name = "localizacio", referencedColumnName = "codi_postal", nullable = false)
    private Localitzacio localitzacio;

    @Lob
    @Column(name = "imatge", columnDefinition = "MEDIUMBLOB")
    private byte[] imatge;
    
    @JoinColumn(nullable = true)
    private String motiu;
    
    @JoinColumn(nullable = true)
    private double importe;
}
