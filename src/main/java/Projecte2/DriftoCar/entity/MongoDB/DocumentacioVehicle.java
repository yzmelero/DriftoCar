package Projecte2.DriftoCar.entity.MongoDB;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import Projecte2.DriftoCar.entity.MySQL.Combustible;
import Projecte2.DriftoCar.entity.MySQL.TipusVehicle;
import Projecte2.DriftoCar.entity.MySQL.Transmisio;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "documentacio_vehicle")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentacioVehicle {
    @Id
    private String matricula;
       
    private String marca;
    
    private String model;
    
    private int any;
    
    private int places;
    
    @Enumerated(EnumType.STRING)
    private Transmisio transmisio; 
            
    @Enumerated(EnumType.STRING)
    private Combustible combustible; // String
    
    @Enumerated(EnumType.STRING)
    private TipusVehicle tipus;

    // 1 = disponible, 0 = no disponible
    private boolean disponibilitat;

    //private document localitzacio
}
