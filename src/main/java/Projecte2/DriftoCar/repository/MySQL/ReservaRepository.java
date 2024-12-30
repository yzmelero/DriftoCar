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
    @Query("""
            SELECT r
            FROM Reserva r
            WHERE (:idReserva IS NULL OR CAST(r.idReserva AS string) LIKE CONCAT('%',
            :idReserva, '%'))
            AND (:email IS NULL OR LOWER(r.client.email) LIKE LOWER(CONCAT('%', :email, '%')))
            AND (:matricula IS NULL OR LOWER(r.vehicle.matricula) LIKE LOWER(CONCAT('%',
            :matricula, '%')))
            """)
    List<Reserva> cercarReserves(@Param("idReserva") Long idReserva, @Param("email") String email, @Param("matricula") String matricula);

    @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.estat = true")
    List<Reserva> findByVehicleMatriculaEstat(@Param("matricula") String matricula);

    @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.dataInici <= :dataFinal")
    List<Reserva> findByVehicleMatriculaData(@Param("matricula") String matricula, @Param("dataFinal") LocalDate dataFinal);

    boolean existsByClientDni(String dni);

    boolean existsByVehicleMatricula(String matricula);

    @Query("""
                SELECT r FROM Reserva r
                WHERE r.client.usuari = :username
                  AND (:searchEmail IS NULL OR LOWER(r.client.email) LIKE LOWER(CONCAT('%', :searchEmail, '%')))
                  AND (:searchId_reserva IS NULL OR CAST(r.idReserva AS string) LIKE CONCAT('%', :searchId_reserva, '%'))
                  AND (:searchMatricula IS NULL OR LOWER(r.vehicle.matricula) LIKE LOWER(CONCAT('%', :searchMatricula, '%')))
            """)
    List<Reserva> findByClientUsuariAndFilters(@Param("username") String username,
            @Param("searchEmail") String searchEmail,
            @Param("searchId_reserva") Long searchId_reserva,
            @Param("searchMatricula") String searchMatricula);

    @Query("""
                SELECT r FROM Reserva r
                WHERE (r.client.usuari = :username OR r.vehicle.matricula IN :matricules)
                  AND (:searchEmail IS NULL OR LOWER(r.client.email) LIKE LOWER(CONCAT('%', :searchEmail, '%')))
                  AND (:searchId_reserva IS NULL OR CAST(r.idReserva AS string) LIKE CONCAT('%', :searchId_reserva, '%'))
                  AND (:searchMatricula IS NULL OR LOWER(r.vehicle.matricula) LIKE LOWER(CONCAT('%', :searchMatricula, '%')))
            """)
    List<Reserva> findByAgentAndFilters(@Param("username") String username,
            @Param("matricules") List<String> matricules,
            @Param("searchEmail") String searchEmail,
            @Param("searchId_reserva") Long searchId_reserva,
            @Param("searchMatricula") String searchMatricula);

}
