/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Anna
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="agent")
@EqualsAndHashCode(callSuper = true)
public class Agent extends Client{

    //enuum
    private Rol rol;
    
    //esto se pone en las columnas que tienen relaciones, se pondra relacion, join column y luego estas 2
    @OneToOne    
    @JoinColumn(name = "localitzacio", referencedColumnName = "codi_postal")  //poner unique     
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Localitzacio localitzacio;
}
