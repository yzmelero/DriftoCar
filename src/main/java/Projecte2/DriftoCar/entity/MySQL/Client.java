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
import jakarta.persistence.Table;
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
@Table(name = "client")
public class Client {
    
    @Id
    private String dni;
    
    private String nom;
    
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
    
    private String adreca;
    
    private String contrasenya;
    
    private boolean premium;
    
}
