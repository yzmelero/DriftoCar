package Projecte2.DriftoCar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DriftoCarApplication {

    @Autowired

    public static void main(String[] args) {
        SpringApplication.run(DriftoCarApplication.class, args);
    }
}



