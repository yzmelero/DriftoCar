/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String matricula;
    
    private String marca;
    
    private String model;
    
    private int any;
    
    private int places;
    
    private Transmisio transmisio; 
            
    private Combustible combustible; // String
    
    private Tipus tipus;

    // 1 = disponible, 0 = no disponible
    private boolean disponibilitat;
    
    private int ubicacio;
    
    
}
