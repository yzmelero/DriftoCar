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
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa una reserva d'un vehicle feta per un client.
 */
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    /**
     * Identificador únic de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    /**
     * Client que realitza la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client", referencedColumnName = "dni", nullable = false)
    private Client client;

    /**
     * Vehicle associat a la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle", referencedColumnName = "matricula", nullable = false)
    private Vehicle vehicle;

    /**
     * Data d'inici de la reserva.
     */
    @Column(name = "data_inici", nullable = false)
    private LocalDate dataInici;

    /**
     * Data de finalització de la reserva.
     */
    @Column(name = "data_fi", nullable = false)
    private LocalDate dataFi;

    /**
     * Cost total de la reserva.
     */
    @Column(name = "cost_total", nullable = false)
    private Double costTotal;

    /**
     * Fiança requerida per a la reserva.
     */
    @Column(name = "fianca", nullable = false)
    private Double fianca;

    /**
     * Estat de la reserva (activa o inactiva).
     */
    @Column(name = "estat", nullable = false)
    private boolean estat;

    /**
     * Data prevista per lliurar el vehicle.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_lliurar")
    private LocalDate dataLliurar;

    /**
     * Descripció de l'estat del vehicle en el moment del lliurament.
     */
    @Column(name = "descripcio_estat_lliurar")
    private String descripcioEstatLliurar;

    /**
     * Data prevista per retornar el vehicle.
     */
    @Column(name = "data_retornar")
    private LocalDate dataRetornar;

    /**
     * Hora d'inici del lloguer.
     */
    @Column(name = "hora_inici")
    private LocalTime horaInici;

    /**
     * Hora prevista per lliurar el vehicle.
     */
    @Column(name = "hora_lliurar")
    private LocalTime horaLliurar;

    /**
     * Hora de finalització del lloguer.
     */
    @Column(name = "hora_fi")
    private LocalTime horaFi;

    /**
     * Hora prevista per retornar el vehicle.
     */
    @Column(name = "hora_retornar")
    private LocalTime horaRetornar;

}
