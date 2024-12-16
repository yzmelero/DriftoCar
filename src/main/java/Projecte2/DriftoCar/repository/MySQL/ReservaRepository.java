/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import java.time.LocalDate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Anna
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Long countAllByIdReserva(Long idReserva);

    List<Reserva> findAll();

    Optional<Reserva> findById(Long Id);

    //Aquesta linia és pel filtre.
    @Query("SELECT r FROM Reserva r "
            + "WHERE (:idReserva IS NULL OR r.idReserva = :idReserva) "
            + "AND (:email IS NULL OR r.client.email = :email)")
    List<Reserva> cercarReserves(@Param("idReserva") Long idReserva, @Param("email") String email);

    @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.estat = true")
    List<Reserva> findByVehicleMatriculaEstat(@Param("matricula") String matricula);

    @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.dataInici <= :dataFinal")
    List<Reserva> findByVehicleMatriculaData(@Param("matricula") String matricula, @Param("dataFinal") LocalDate dataFinal);

}
