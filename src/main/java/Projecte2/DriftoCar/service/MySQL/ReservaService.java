/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;

/**
 *
 * @author Mario
 */
@Service
public class ReservaService {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     *  Aquest metode guarda una nova reserva a a BBDD si el client i la matricula existeixen
     * 
     * @param reserva
     * @return
     * @throws Exception
     * @author Mario
     */
    public Reserva altaReserva(Reserva reserva) {
        log.info("S'ha entrat al metode d'altaReserva.");

        // Verificar que el vehículo existe
        Optional<Vehicle> vehicle = vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula());

            
        // Verificar que el cliente existe
        Optional<Client> client = clientRepository.findByDni(reserva.getClient().getDni());

        if (client.isEmpty()) {
            throw new RuntimeException("El client no existeix");
        }
        if (vehicle.isEmpty()) {
            throw new RuntimeException("El vehicle no existeix");
        }

        Vehicle vehicleNou = vehicle.get();
        if (!vehicleNou.isDisponibilitat()) {
            throw new RuntimeException("El vehicle no esta disponible");
        }

        reserva.setClient(client.get());
        reserva.setVehicle(vehicle.get());

        // Guardar la reserva en la base de datos
        return reservaRepository.save(reserva);
    }
    
    public List<Reserva> llistarReservas(){
        return reservaRepository.findAll();
    }

    public List<Reserva> cercarReserva(String email, Long id_reserva) {
        log.debug("cercarReserva() - email: " + email + ", id_reserva: " + id_reserva);
        return reservaRepository.cercarReserves(id_reserva, email);
        
    }
}
