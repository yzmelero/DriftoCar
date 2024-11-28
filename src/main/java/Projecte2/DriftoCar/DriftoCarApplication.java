package Projecte2.DriftoCar;

import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // Indica que esta es la clase principal de la aplicación Spring Boot
public class DriftoCarApplication {

    public static void main(String[] args) {
        // Inicia la aplicación Spring Boot y obtiene el contexto de la aplicación
        ApplicationContext context = SpringApplication.run(DriftoCarApplication.class, args);

        // Obtener el bean del servicio LocalitzacioService desde el contexto
        LocalitzacioService localitzacioService = context.getBean(LocalitzacioService.class);

        String codiPostal = "08001"; // Cambia este valor por un código postal válido que quieras probar

        try {
            // Llamamos al servicio para intentar eliminar la localización
            localitzacioService.baixaLocalitzacio(codiPostal);
            System.out.println("Localització eliminada correctament");
        } catch (RuntimeException e) {
            // Si se lanza una excepción, la mostramos
            System.err.println("Error: " + e.getMessage());
        }
    }
}
