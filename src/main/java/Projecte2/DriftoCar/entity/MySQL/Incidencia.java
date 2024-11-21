/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "incidencia")
public class Incidencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //si es 1 sera oberta
    private boolean estat;
    
    private String motiu;
    
    private double cost;

    private int periode;
    
    @ManyToOne
    @JoinColumn(name = "matricula", referencedColumnName = "matricula")       
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String matricula;
}
