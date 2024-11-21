/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Anna
 */
@Entity
@Table(name = "localitzacio")
public class Localitzacio {
    
    @Id
    private int codiPostal;
    
    private String ciutat;
    
    @Column(name = "adreca_localitzacio")
    private String adrecaLocalitzacio;
    
    private String horari;
    
    private String condicions;
    

}
