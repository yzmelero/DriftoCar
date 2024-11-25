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
import lombok.NoArgsConstructor;

/**
 *
 * @author Anna
 */
@Entity
@Table(name = "localitzacio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Localitzacio {
    
    @Id
    @Column(name = "codi_postal")
    private Long codiPostal;
    
    private String ciutat;
    
    @Column(name = "adreca_localitzacio")
    private String adrecaLocalitzacio;
    
    private String horari;
    
    private String condicions;
    
    @OneToMany(mappedBy = "localitzacio")
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToOne(mappedBy = "localitzacio")
    private Agent agent;
    
    
    
}
