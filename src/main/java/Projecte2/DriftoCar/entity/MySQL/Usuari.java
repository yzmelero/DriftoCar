/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mario
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Usuari {

    @Id
    @Column(name = "DNI")
    private String dni;
    
    private String usuari;
    
    private String contrasenya;

    private String nom;

    private String cognoms;
    
    private String adreça;

    private String email;
    
    @Column(name = "carnet_conduir")
    private String carnetConduir;
    
}
