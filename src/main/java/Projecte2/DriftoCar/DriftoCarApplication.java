package Projecte2.DriftoCar;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.TipusRol;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;

/**
 * Classe principal de l'aplicació DriftoCar.
 * Aquesta classe inicia l'aplicació Spring Boot i carrega les dades d'exemple a
 * la base de dades.
 */
@SpringBootApplication
public class DriftoCarApplication {

	@Autowired
	private static ClientService clientService;

	@Autowired
	private static ClientRepository clientRepository;

	@Autowired
	private static LocalitzacioService localitzacioService;

	@Autowired
	private static LocalitzacioRepository localitzacioRepository;

	@Autowired
	private static AgentService agentService;

	/**
	 * Punt d'entrada principal de l'aplicació.
	 * Inicialitza el context de Spring i carrega dades d'exemple a la base de
	 * dades.
	 *
	 * @param args Arguments de línia de comandes.
	 */
	public static void main(String[] args) {

		Logger log = LoggerFactory.getLogger(DriftoCarApplication.class);

		log.info("Iniciant aplicació DriftoCar...");

		var context = SpringApplication.run(DriftoCarApplication.class, args);
		localitzacioService = context.getBean(LocalitzacioService.class);
		localitzacioRepository = context.getBean(LocalitzacioRepository.class);
		clientService = context.getBean(ClientService.class);
		clientRepository = context.getBean(ClientRepository.class);
		agentService = context.getBean(AgentService.class);

		log.info("Afegint localitzacions d'exemple...");
		// Llenar bbdd con ejemplos
		Localitzacio localitzacio = new Localitzacio(
				"80812", "Madrid",
				"LaCasaDeLaCasa",
				"13:00 - 20:29", "Carrer",
				null, null);
		if (localitzacioRepository.findById(localitzacio.getCodiPostal()).isEmpty()) {
			localitzacioService.altaLocalitzacio(localitzacio);
			log.info("Localització afegida: {}", localitzacio);
		}

		Localitzacio localitzacio2 = new Localitzacio(
				"90929", "Barcelona",
				"En Una Mansion",
				"00:00 - 23:59", "Entrada de la mansion",
				null, null);
		if (localitzacioRepository.findById(localitzacio2.getCodiPostal()).isEmpty()) {
			localitzacioService.altaLocalitzacio(localitzacio2);
			log.info("Localització afegida: {}", localitzacio);
		}

		log.info("Afegint clients d'exemple...");
		Client client = new Client("45985381D", true, "client", "client",
				"cliente", "apellidoCliente", "clienteDefault@gmail.com", "123456789",
				"Espanya", "c1", LocalDate.parse("2025-12-12"),
				LocalDate.parse("2025-12-12"), "7893562713482934", "La calle del cliente",
				true, null);

		try {
			if (clientRepository.findById(client.getDni()).isEmpty()) {
				clientService.altaClient(client);
				log.info("Client afegit: {}", client);

			}
		} catch (Exception e) {
			log.error("Error afegint client: {}", e.getMessage());
		}

		log.info("Afegint agents d'exemple...");
		Agent agent = new Agent("47544582V", "admin", "admin", "administrador",
				"apellidoAdministrador",
				"adminDefault@gmail.com", "654993670", "Espanya", "a1",
				LocalDate.parse("2025-12-12"),
				LocalDate.parse("2025-12-12"), "8937256791027345", "La calle del Admin", false,
				null, TipusRol.ADMIN,
				localitzacio);
		try {
			agentService.altaAgent(agent);
			log.info("Agent afegit: {}", agent);
		} catch (Exception e) {
			log.error("Error afegint agent: {}", e.getMessage());
		}

		Agent agent2 = new Agent("89123179Z", "agent", "agent", "agente",
				"apellidoAgente",
				"agenteDefault@gmail.com", "640189094", "Mèxic", "g1",
				LocalDate.parse("2025-12-12"),
				LocalDate.parse("2025-12-12"), "7412369845632178", "La calle del agente",
				false, null, TipusRol.AGENT,
				localitzacio2);
		try {
			agentService.altaAgent(agent2);
			log.info("Agent afegit: {}", agent2);
		} catch (Exception e) {
			log.error("Error afegint agent: {}", e.getMessage());
		}

		log.info("Inicialització finalitzada amb èxit.");

	}

}
