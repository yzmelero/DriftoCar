package Projecte2.DriftoCar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;

@Controller
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private LocalitzacioService localitzacioService;

    @GetMapping("/llistar")
    public String llistarAgents(Model model) {
        List<Agent> agents = agentService.llistarAgents();
        model.addAttribute("agents", agents);
        return "agent-llistar"; // Nombre del archivo HTML
    }
    
@GetMapping("/alta")
public String mostrarFormulario(Model model) {
    Agent agent = new Agent();
    agent.setLocalitzacio(new Localitzacio()); // Inicializar localitzacio
    model.addAttribute("agent", agent);

    // Obtener localitzacions desde la BBDD
    List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
    model.addAttribute("localitzacions", localitzacions);

    return "agent-alta"; // Ajusta según el nombre de tu template
}

    @PostMapping("/guardar")
    public String guardarAgente(@ModelAttribute Agent agent) {
        agentService.altaAgent(agent);
        return "redirect:/agent/listar";
    }
}

