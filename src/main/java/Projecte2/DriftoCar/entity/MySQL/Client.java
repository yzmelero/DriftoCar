/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anna
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client extends Usuari{
    
    @Id
    private String dni;
    
    @Column
    private String nom;
    
    @Column
    private String cognoms;
    
    @Column(unique = true)
    private String email;
    
    @Column(unique = true)
    private String llicencia;
     
    @Column(name = "llic_caducitat")
    private LocalDate llicCaducitat;
    
    @Column(name = "dni_caducitat")
    private LocalDate dniCaducitat;
    
    @Column(name = "num_tarjeta_credit", unique = true)
    private int numTarjetaCredit;
    
    @Column
    private String adreca;
    
    @Column
    private String contrasenya;
    
    
}
