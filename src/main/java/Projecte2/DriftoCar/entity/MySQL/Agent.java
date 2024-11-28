/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MySQL;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "agent")
@EqualsAndHashCode(callSuper = true)
public class Agent extends Client {

    @Enumerated(EnumType.STRING)
    private TipusRol rol;

    // esto se pone en las columnas que tienen relaciones, se pondra relacion, join
    // column y luego estas 2
    @OneToOne
    @JoinColumn(name = "localitzacio", referencedColumnName = "codi_postal")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Localitzacio localitzacio;

    public Agent(String dni, String usuari, String contrasenya, String nom, String cognoms,
            String email, String llicencia, LocalDate llicCaducitat, LocalDate dniCaducitat,
            Long numTarjetaCredit, String adreca, boolean reputacio, List<Reserva> reserva,
            TipusRol rol, Localitzacio localitzacio) {
        super(dni, usuari, contrasenya, nom, cognoms, email, llicencia, llicCaducitat,
            dniCaducitat, numTarjetaCredit, adreca, reputacio, reserva);
        this.rol = rol;
        this.localitzacio = localitzacio;
    }

    

}
