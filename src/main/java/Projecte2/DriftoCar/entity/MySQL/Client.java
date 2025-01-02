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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import Projecte2.DriftoCar.entity.Permis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Classe que representa un client dins del sistema.
 * Aquesta classe utilitza herència per suportar altres tipus d'usuaris, com ara
 * agents.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client implements UserDetails {

    /**
     * DNI del client. Actua com a identificador únic.
     * Ha de tenir un format de 8 números i una lletra majúscula.
     */
    @Id
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI ha de tenir 8 números i una lletra majúscula.")
    private String dni;

    /**
     * Estat del client (actiu/inactiu).
     */
    private boolean activo;

    /**
     * Nom d'usuari únic.
     */
    @Column(unique = true)
    private String usuari;

    /**
     * Contrasenya del client.
     */
    private String contrasenya;

    /**
     * Nom del client.
     */
    private String nom;

    /**
     * Cognoms del client.
     */
    private String cognoms;

    /**
     * Correu electrònic únic.
     */
    @Column(unique = true)
    private String email;

    /**
     * Telèfon de contacte, que ha de tenir 9 dígits.
     */
    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{9}$", message = "El telèfon ha de tenir 9 dígits.")
    private String telefon;

    /**
     * Nacionalitat del client.
     */
    @Column
    private String nacionalitat;

    /**
     * Llicència del client.
     */
    private String llicencia;

    /**
     * Data de caducitat de la llicència.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "llic_caducitat")
    private LocalDate llicCaducitat;

    /**
     * Data de caducitat del DNI.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dni_caducitat")
    private LocalDate dniCaducitat;

    /**
     * Número de targeta de crèdit únic.
     */
    @Column(name = "num_tarjeta_credit", unique = true)
    private String numTarjetaCredit;

    /**
     * Adreça del client.
     */
    private String adreca;

    /**
     * Reputació del client (1 = premium, 0 = normal).
     */
    private boolean reputacio;

    /**
     * Llista de reserves associades al client.
     */
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reserva = new ArrayList<>();

    /**
     * Retorna els permisos associats al client.
     * Els permisos varien depenent de si el client és un agent o un usuari
     * estàndard.
     *
     * @return una col·lecció d'objectes {@link GrantedAuthority} que representen
     *         els permisos del client.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> ret = new ArrayList<>();

        if (this instanceof Agent) {
            Agent agent = (Agent) this;
            ret.add(new Permis(agent.getRol().toString()));
        } else {
            ret.add(new Permis("ROLES_CLIENT"));
        }

        return ret;
    }

    /**
     * Retorna la contrasenya del client.
     * Aquest mètode és utilitzat per Spring Security per autenticar l'usuari.
     *
     * @return la contrasenya del client.
     */
    @Override
    public String getPassword() {
        return getContrasenya();
    }

    /**
     * Retorna el nom d'usuari del client.
     * Aquest mètode és utilitzat per Spring Security per identificar l'usuari.
     *
     * @return el nom d'usuari del client.
     */
    @Override
    public String getUsername() {
        return getUsuari();
    }

}
