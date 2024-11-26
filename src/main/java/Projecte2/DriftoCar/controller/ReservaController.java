package Projecte2.DriftoCar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.service.MySQL.ReservaService;

/**
 * @author mario
 */
@Controller
@RequestMapping("/reserva")
public class ReservaController {
@Autowired
private ReservaService reservaService;

    @GetMapping("/listar")
public String listarReservas(Model model) {
    List<Reserva> reservas = reservaService.listarReservas();
    model.addAttribute("reservas", reservas);
    return "reserva-listar";
}
}
