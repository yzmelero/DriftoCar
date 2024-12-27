/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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

    // 1 = actiu, 0 = inactiu
    private boolean estat;

    private String motiu;

    @Column(name = "data_inici_incidencia")
    private LocalDateTime dataIniciIncidencia;

    @Column(name = "data_fi_incidencia")
    private LocalDateTime dataFiIncidencia;

    @Column(name = "descripcio")
    private String descripcio;

    @ManyToOne
    @JoinColumn(name = "matricula", referencedColumnName = "matricula", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehicle matricula;
}
