/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Classe que representa la localització d'un punt de recollida.
 */
@Entity
@Table(name = "localitzacio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Localitzacio {
    
    /**
     * Codi postal que identifica únicament una localització.
     */
    @Id
    @Column(name = "codi_postal")
    private String codiPostal;
    
    /**
     * Ciutat on es troba la localització.
     */
    private String ciutat;
    
    /**
     * Adreça física de la localització.
     */
    @Column(name = "adreca_localitzacio")
    private String adrecaLocalitzacio;
    
    /**
     * Horari d'obertura i tancament de la localització.
     */
    private String horari;

    /**
     * Condicions o normes associades a la localització.
     */
    private String condicions;
    
    /**
     * Llista de vehicles associats a la localització.
     */
    @OneToMany(mappedBy = "localitzacio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Vehicle> vehicles = new ArrayList<>();

    /**
     * Agent responsable d'aquesta localització.
     */
    @OneToOne(mappedBy = "localitzacio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Agent agent;
    
    
    
}
