package Projecte2.DriftoCar.entity;

import org.springframework.security.core.GrantedAuthority;

public class Permis implements GrantedAuthority {
    private String permis;
    
    public Permis(String permis){
        this.permis = permis;
    }

    @Override
    public String getAuthority(){
        return permis;
    }
}
