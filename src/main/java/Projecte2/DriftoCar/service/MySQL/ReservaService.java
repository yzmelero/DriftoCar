/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.time.LocalDate;

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

    public Reserva createReserva(String matricula, String dni, LocalDate dataInici, LocalDate dataFi, double costTotal, double fianca) {
        // Verificar que el vehículo existe
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("El vehículo no existe"));

        // Verificar que el cliente existe
        Client client = clientRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("El cliente no existe"));

        // Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setVehicle(vehicle);
        reserva.setClient(client);
        reserva.setDataInici(dataInici);
        reserva.setDataFi(dataFi);
        reserva.setDataFi(dataFi);
        reserva.setCostTotal(costTotal);
        reserva.setFianca(fianca);
        reserva.setEstat(true);

        // Guardar la reserva en la base de datos
        return reservaRepository.save(reserva);
    }
}
