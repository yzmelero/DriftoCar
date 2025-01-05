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
 * Classe que representa un agent del sistema, el qual hereta de la classe
 * Client.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agent")
@EqualsAndHashCode(callSuper = true)
public class Agent extends Client {

    /**
     * Rol de l'agent dins del sistema.
     */
    @Enumerated(EnumType.STRING)
    private TipusRol rol;

    /**
     * Localització associada a l'agent. Relació One-to-One amb la classe
     * Localitzacio.
     */
    @OneToOne
    @JoinColumn(name = "localitzacio", referencedColumnName = "codi_postal")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Localitzacio localitzacio;

    /**
     * Constructor complet per inicialitzar un objecte Agent amb els seus atributs i
     * els heretats.
     *
     * @param dni              DNI de l'agent.
     * @param usuari           Nom d'usuari.
     * @param contrasenya      Contrasenya de l'agent.
     * @param nom              Nom de l'agent.
     * @param cognoms          Cognoms de l'agent.
     * @param email            Correu electrònic de l'agent.
     * @param telefon          Telèfon de contacte.
     * @param nacionalitat     Nacionalitat de l'agent.
     * @param llicencia        Llicència de l'agent.
     * @param llicCaducitat    Data de caducitat de la llicència.
     * @param dniCaducitat     Data de caducitat del DNI.
     * @param numTarjetaCredit Número de targeta de crèdit.
     * @param adreca           Adreça de l'agent.
     * @param reputacio        Reputació del client (1 = premium, 0 = normal).
     * @param reserva          Llista de reserves associades.
     * @param rol              Rol de l'agent.
     * @param localitzacio     Localització associada.
     */
    public Agent(String dni, String usuari, String contrasenya, String nom, String cognoms,
            String email, String telefon, String nacionalitat, String llicencia, LocalDate llicCaducitat,
            LocalDate dniCaducitat,
            String numTarjetaCredit, String adreca, boolean reputacio, List<Reserva> reserva,
            TipusRol rol, Localitzacio localitzacio) {
        super(dni, true, usuari, contrasenya, nom, cognoms, email, telefon, nacionalitat, llicencia, llicCaducitat,
                dniCaducitat, numTarjetaCredit, adreca, reputacio, reserva);
        this.rol = rol;
        this.localitzacio = localitzacio;
    }

}
