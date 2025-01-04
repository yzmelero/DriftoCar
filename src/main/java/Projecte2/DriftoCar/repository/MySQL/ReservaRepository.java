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
 * Repositori per gestionar les operacions CRUD de la taula `Reserva` a la base
 * de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
        /**
         * Comptabilitza el nombre de reserves amb un ID de reserva específic.
         *
         * @param idReserva l'ID de la reserva.
         * @return el nombre de reserves trobades.
         */
        Long countAllByIdReserva(Long idReserva);

        /**
         * Retorna totes les reserves.
         *
         * @return una llista de reserves.
         */
        List<Reserva> findAll();

        /**
         * Cerca una reserva pel seu ID.
         *
         * @param id l'ID de la reserva.
         * @return un {@link Optional} que conté la reserva si es troba, buit en cas
         *         contrari.
         */
        Optional<Reserva> findById(Long Id);

        /**
         * Cerca reserves segons criteris opcionals de filtre: ID de reserva, email del
         * client o matrícula del vehicle.
         *
         * @param idReserva l'ID de la reserva (opcional).
         * @param email     l'email del client (opcional).
         * @param matricula la matrícula del vehicle (opcional).
         * @return una llista de reserves que compleixin els criteris.
         */
        @Query("""
                        SELECT r
                        FROM Reserva r
                        WHERE (:idReserva IS NULL OR CAST(r.idReserva AS string) LIKE CONCAT('%',
                        :idReserva, '%'))
                        AND (:email IS NULL OR LOWER(r.client.email) LIKE LOWER(CONCAT('%', :email, '%')))
                        AND (:matricula IS NULL OR LOWER(r.vehicle.matricula) LIKE LOWER(CONCAT('%',
                        :matricula, '%')))
                        """)
        List<Reserva> cercarReserves(@Param("idReserva") Long idReserva, @Param("email") String email,
                        @Param("matricula") String matricula);

        /**
         * Cerca reserves actives per matrícula del vehicle.
         *
         * @param matricula la matrícula del vehicle.
         * @return una llista de reserves actives associades a la matrícula.
         */
        @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.estat = true")
        List<Reserva> findByVehicleMatriculaEstat(@Param("matricula") String matricula);

        /**
         * Cerca reserves d'un vehicle amb una data final específica.
         *
         * @param matricula la matrícula del vehicle.
         * @param dataFinal la data final.
         * @return una llista de reserves que compleixin els criteris.
         */
        @Query("SELECT r FROM Reserva r WHERE r.vehicle.matricula = :matricula AND r.dataInici <= :dataFinal")
        List<Reserva> findByVehicleMatriculaData(@Param("matricula") String matricula,
                        @Param("dataFinal") LocalDate dataFinal);

        /**
         * Verifica si existeix una reserva associada al DNI d'un client.
         *
         * @param dni el DNI del client.
         * @return true si existeix, false en cas contrari.
         */
        boolean existsByClientDni(String dni);

        /**
         * Verifica si existeix una reserva associada a la matrícula d'un vehicle.
         *
         * @param matricula la matrícula del vehicle.
         * @return true si existeix, false en cas contrari.
         */
        boolean existsByVehicleMatricula(String matricula);

        /**
         * Cerca reserves d'un client segons diversos criteris opcionals.
         *
         * @param username         el nom d'usuari del client.
         * @param searchEmail      l'email del client (opcional).
         * @param searchId_reserva l'ID de la reserva (opcional).
         * @param searchMatricula  la matrícula del vehicle (opcional).
         * @return una llista de reserves que compleixin els criteris.
         */
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

        /**
         * Cerca reserves associades a un agent o als seus vehicles, segons diversos
         * criteris opcionals.
         *
         * @param username         el nom d'usuari de l'agent.
         * @param matricules       una llista de matrícules gestionades per l'agent.
         * @param searchEmail      l'email del client (opcional).
         * @param searchId_reserva l'ID de la reserva (opcional).
         * @param searchMatricula  la matrícula del vehicle (opcional).
         * @return una llista de reserves que compleixin els criteris.
         */
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
