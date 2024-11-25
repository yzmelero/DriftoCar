/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;

/**
 *
 * @author Anna
 */
public class ReservaService {

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
    public Reserva createReserva(Reserva reserva) throws Exception {
        // Verificar que el vehículo existe
        Optional<Vehicle> vehicle = vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula());

            
        // Verificar que el cliente existe
        Optional<Client> client = clientRepository.findByDni(reserva.getClient().getDni());

        if (client == null) {
            throw new Exception("El client no existeix");
        }
        if (vehicle == null) {
            throw new Exception("El vehicle no existeix");
        }
        // Guardar la reserva en la base de datos
        return reservaRepository.save(reserva);
    }
}
