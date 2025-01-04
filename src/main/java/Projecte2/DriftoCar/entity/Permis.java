package Projecte2.DriftoCar.entity;

import org.springframework.security.core.GrantedAuthority;

/**
 * Classe que representa un permís o autorització dins del sistema de seguretat.
 * Implementa la interfície {@link GrantedAuthority}.
 */
public class Permis implements GrantedAuthority {
    /**
     * Nom del permís o rol associat.
     */
    private String permis;

    /**
     * Constructor que inicialitza el permís amb el valor especificat.
     *
     * @param permis el nom del permís o rol.
     */
    public Permis(String permis) {
        this.permis = permis;
    }

    /**
     * Retorna el nom del permís o rol associat.
     *
     * @return una cadena amb el nom del permís.
     */
    @Override
    public String getAuthority() {
        return permis;
    }
}
