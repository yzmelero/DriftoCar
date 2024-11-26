/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Projecte2.DriftoCar.entity.MySQL.Reserva;

/**
 *
 * @author Anna
 */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Long countAllByIdReserva(Long idReserva);

    List<Reserva> findAll();

    Optional<Reserva> findById(Long Id);
}
