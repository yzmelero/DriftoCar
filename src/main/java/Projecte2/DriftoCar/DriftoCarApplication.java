package Projecte2.DriftoCar;

import java.time.LocalDate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.TipusCombustible;
import Projecte2.DriftoCar.entity.MySQL.TipusTransmisio;
import Projecte2.DriftoCar.entity.MySQL.TipusVehicle;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MySQL.ReservaService;


@SpringBootApplication
public class DriftoCarApplication {

	public static void main(String[] args) {
		SpringApplication.run(DriftoCarApplication.class, args);
		Client client = new Client("47544582V", "straffe", "1234", "mario",
		 "olaya", "mariolayap@gmail.com", "47544582V",
		 LocalDate.parse("2024-12-12"), LocalDate.parse("2024-12-12"),
		323243233232l, "mi casa", false, null);

		Vehicle vehicle = new Vehicle("1977bwl", "mercedes", "a7", 2024,
		 5, TipusTransmisio.AUTOMÀTIC, TipusCombustible.BENZINA, TipusVehicle.COTXE,
		  true, null);

		Reserva reserva = new Reserva(null, client, vehicle, LocalDate.parse("2024-12-12"),
		 LocalDate.parse("2024-12-12"), 2000,
		  200, true);
		  ReservaService.altaReserva(reserva);
	}

}
