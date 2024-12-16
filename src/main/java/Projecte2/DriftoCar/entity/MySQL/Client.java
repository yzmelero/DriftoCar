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
 *
 * @author Anna
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client implements UserDetails{
    
    @Id
    private String dni;

    @Column(unique = true)
    private String usuari;
    
    private String contrasenya;
        
    private String nom;
    
    private String cognoms;
    
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String telefon;

    @Column
    private String nacionalitat;
    
    private String llicencia;
     
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "llic_caducitat")
    private LocalDate llicCaducitat;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dni_caducitat")
    private LocalDate dniCaducitat;
    
    @Column(name = "num_tarjeta_credit", unique = true)
    private String numTarjetaCredit;
    
    private String adreca;
    
    //1 = premium, 0 = normal
    private boolean reputacio;
    
    @OneToMany(mappedBy = "client")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reserva = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> ret = new ArrayList<>();

        if (this instanceof Agent) {
            Agent agent = (Agent) this;
            ret.add(new Permis( agent.getRol().toString()));
        }else{
            ret.add(new Permis("CLIENT"));
        }
        
        return ret;
    }

    @Override
    public String getPassword() {
        return getContrasenya();
    }

    @Override
    public String getUsername() {
        return getUsuari();
    }
    
    
}
