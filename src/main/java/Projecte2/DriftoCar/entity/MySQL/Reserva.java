/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.types.Binary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anna
 */
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_reserva")
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client", referencedColumnName = "dni", nullable = false) 
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "vehicle", referencedColumnName = "matricula", nullable = false) 
    private Vehicle vehicle;

    @Column(name = "data_inici", nullable = false)
    private LocalDate dataInici;

    @Column(name = "data_fi", nullable = false)
    private LocalDate dataFi;

    @Column(name = "cost_total", nullable = false)
    private double costTotal; 

    @Column(name = "fianca", nullable = false)
    private double fianca;
    
    @Column(name = "estat", nullable = false)
    private boolean estat;
  
    @Column(name = "data_hora_lliurar")
    private LocalDateTime dataHoraLliurar;

    @Column(name = "descripcio_estat_lliurar")
    private String descripcioEstatLliurar;

    @Column(name = "data_hora_retornar")
    private LocalDateTime dataHoraRetornar;


}
