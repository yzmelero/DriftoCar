package Projecte2.DriftoCar;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.TipusRol;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;

@SpringBootApplication
public class DriftoCarApplication {

	/*
	 * @Autowired
	 * private static ClientService clientService;
	 * 
	 * @Autowired
	 * private static LocalitzacioService localitzacioService;
	 * 
	 * @Autowired
	 * private static AgentService agentService;
	 */

	public static void main(String[] args) {
		SpringApplication.run(DriftoCarApplication.class, args);

		//TODO arreglar el main para el script inicial, con un if en cada servicio de alta
		/*
		 * var context = SpringApplication.run(DriftoCarApplication.class, args);
		 * localitzacioService = context.getBean(LocalitzacioService.class);
		 * clientService = context.getBean(ClientService.class);
		 * agentService = context.getBean(AgentService.class);
		 * // Llenar bbdd con ejemplos
		 * 
		 * Localitzacio localitzacio = new Localitzacio(
		 * "80812", "Madrid",
		 * "LaCasaDeLaCasa",
		 * "20:00-22:30", "Carrer",
		 * null, null);
		 * localitzacioService.altaLocalitzacio(localitzacio);
		 * Localitzacio localitzacio2 = new Localitzacio(
		 * "90929", "Barcelona",
		 * "En Una Mansion",
		 * "00:00-23:59", "Entrada de la mansion",
		 * null, null);
		 * localitzacioService.altaLocalitzacio(localitzacio2);
		 * 
		 * Client client = new Client("47544582C", "client", "client",
		 * "cliente", "apellidoCliente", "clienteDefault@gmail.com", "123456789",
		 * "CienteLandia", "c1", LocalDate.parse("2024-12-12"),
		 * LocalDate.parse("2024-12-12"), "999316536728", "La calle del cliente",
		 * true, null);
		 * 
		 * try {
		 * clientService.altaClient(client);
		 * } catch (Exception e) {
		 * 
		 * }
		 * 
		 * Agent agent = new Agent("63535312A", "admin", "admin", "administrador",
		 * "apellidoAdministrador",
		 * "adminDefault@gmail.com", "654993670", "AdminLandia", "a1",
		 * LocalDate.parse("2024-12-12"),
		 * LocalDate.parse("2024-12-12"), "766737631552", "La calle del Admin", false,
		 * null, TipusRol.ADMIN,
		 * localitzacio);
		 * agentService.altaAgent(agent);
		 * 
		 * Agent agent2 = new Agent("99462678G", "agent", "agent", "agente",
		 * "apellidoAgente",
		 * "agenteDefault@gmail.com", "640189094", "AgenteLandia", "g1",
		 * LocalDate.parse("2024-12-12"),
		 * LocalDate.parse("2024-12-12"), "38475621001893", "La calle del agente",
		 * false, null, TipusRol.AGENT,
		 * localitzacio2);
		 * agentService.altaAgent(agent2);
		 */
	}

}
