package Projecte2.DriftoCar.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioClient;
import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioClientRepository;
import Projecte2.DriftoCar.repository.MySQL.AgentRepository;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import jakarta.validation.Valid;

/**
 * Controlador per gestionar les operacions relacionades amb els agents.
 * 
 * Aquesta classe conté els mètodes necessaris per gestionar les funcionalitats següents:
 * - Modificar agents existents.
 * - Eliminar agents.
 * - Consultar els detalls d'un agent.
 * 
 * Els mètodes inclouen validacions, tractament de fitxers, gestió d'errors i interaccions 
 * amb el servei i el repositori de documentació.
 * 
 * Dependències:
 * - {@code AgentService}: Per a la gestió de la lògica de negoci dels agents.
 * - {@code DocumentacioClientRepository}: Per gestionar la documentació associada als agents.
 * - {@code PasswordEncoder}: Per gestionar l'encriptació de contrasenyes.
 * 
 */
@Controller
@RequestMapping("/agent")
@Scope("session")
public class AgentController {

    Logger log = LoggerFactory.getLogger(AgentController.class);
    @Autowired
    private AgentService agentService;
    @Autowired
    private LocalitzacioService localitzacioService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DocumentacioClientRepository documentacioClientRepository;
    @Autowired
    private AgentRepository agentRepository;

    /**
     * Mostra el llistat d'agents, amb la possibilitat de filtrar per DNI.
     *
     * @param dni   El DNI per filtrar. Si no s'especifica, es mostren tots els
     *              agents.
     * @param model El model per passar dades a la vista.
     * @return El nom de la plantilla Thymeleaf per al llistat d'agents.
     */
    @GetMapping("/llistar")
    public String llistarAgents(@RequestParam(value = "dni", required = false) String dni, Model model) {

        log.info("Entrant al mètode llistarAgents amb el filtre DNI: {}", dni);
        List<Agent> agents;

        if (dni != null && !dni.isEmpty()) {
            log.debug("Filtrant els agents pel DNI: {}", dni);
            agents = agentService.filtrarPerDni(dni); // Cerca agents pel DNI
        } else {
            log.debug("Recuperant tots els agents.");
            agents = agentService.llistarAgents(); // Mostra tots els agents
        }
        model.addAttribute("filtroDni", dni);
        model.addAttribute("agents", agents);
        log.info("S'ha completat el llistat d'agents.");
        return "agent-llistar"; // Nombre del archivo HTML
    }

    /**
     * Mostra el formulari per donar d'alta un nou agent.
     *
     * @param model El model per passar dades a la vista.
     * @return La vista del formulari per donar d'alta un agent.
     */
    @GetMapping("/alta")
    public String altaAgente(Model model) {

        log.info("Entrant al mètode altaAgente.");
        if (!model.containsAttribute("agent")) {
            model.addAttribute("agent", new Agent());
            List<Localitzacio> localitzacions = agentService.getLocalitzacionsDisponibles();
            model.addAttribute("localitzacions", localitzacions);
            log.debug("S'han carregat les localitzacions disponibles.");
        }
        return "agent-alta";
    }

    /**
     * Guarda un nou agent a la base de dades.
     *
     * @param agent           L'agent a guardar.
     * @param bindingResult   El resultat de la validació.
     * @param model           El model per passar dades a la vista.
     * @param imatgeDni       La imatge del DNI.
     * @param imatgeLlicencia La imatge de la llicència.
     * @return La vista del llistat d'agents si tot és correcte, o el formulari
     *         d'alta si hi ha errors.
     */
    @PostMapping("/guardar")
    public String guardarAgente(@Valid Agent agent, BindingResult bindingResult, Model model,
            @RequestParam(value = "imatgeDni", required = false) MultipartFile imatgeDni,
            @RequestParam(value = "imatgeLlicencia", required = false) MultipartFile imatgeLlicencia) {

        log.info("Entrant al mètode guardarAgente.");
        if (bindingResult.hasErrors()) {
            log.warn("Errors de validació detectats al formulari de l'agent.");
            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            model.addAttribute("agent", agent);
            return "agent-alta";
        }

        String contrasenya = agent.getContrasenya();

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusYears(50);

        if (agent.getDniCaducitat().isBefore(currentDate) || agent.getDniCaducitat().isAfter(maxDate)) {
            log.error("La data d'expiració del DNI no és vàlida.");
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("agent", agent);
            return "agent-alta"; // Nombre de tu vista HTML
        }

        if (agent.getLlicCaducitat().isBefore(currentDate) || agent.getLlicCaducitat().isAfter(maxDate)) {
            log.error("La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("error", "La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("agent", agent);
            return "agent-alta";
        }
        try {
            agentService.altaAgent(agent);
            log.debug("S'ha donat d'alta l'agent amb DNI: {}", agent.getDni());

            DocumentacioClient documentacio = new DocumentacioClient();
            documentacio.setDni(agent.getDni());

            if (imatgeDni != null && !imatgeDni.isEmpty()) {
                documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDni.getBytes()) });
            }
            if (imatgeLlicencia != null && !imatgeLlicencia.isEmpty()) {
                documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicencia.getBytes()) });
            }
            documentacioClientRepository.save(documentacio);
        } catch (Exception e) {
            log.error("Error en donar d'alta l'agent: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            agent.setContrasenya(contrasenya);
            model.addAttribute("agent", agent); // Mantiene los demás datos en el formulario

            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            return "agent-alta";
        }

        log.info("L'agent s'ha guardat correctament.");
        return "redirect:/agent/llistar"; // Redirige al listado si todo es correcto
    }

    /**
     * Mostra el formulari per modificar un agent.
     *
     * @param dni   El DNI de l'agent a modificar.
     * @param model El model per passar dades a la vista.
     * @return La vista del formulari de modificació d'un agent.
     */
    @GetMapping("/modificar/{dni}")
    public String modificarAgent(@PathVariable("dni") String dni, Model model) {

        log.info("Entrant al mètode modificarAgent per al DNI: {}", dni);
        Agent agent = agentService.obtenirAgentPerDni(dni);
        if (agent == null) {
            log.error("No existeix cap agent amb el DNI: {}", dni);
            throw new RuntimeException("No existeix cap agent amb aquest DNI.");

        }
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);
        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
            }
        }

        model.addAttribute("agent", agent);
        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);

        log.info("Formulari carregat per a l'agent amb DNI: {}", dni);
        return "agent-modificar";
    }

    /**
     * Modifica les dades d'un agent existent i actualitza la seva documentació.
     *
     * @param agent               L'agent amb les dades actualitzades.
     * @param imatgeDniFile       Fitxer de la imatge del DNI, opcional.
     * @param imatgeLlicenciaFile Fitxer de la imatge de la llicència, opcional.
     * @param model               El model per passar dades i missatges d'error a la
     *                            vista.
     * @return La pàgina de modificació amb missatges d'error o la redirecció a la
     *         llista d'agents.
     */
    @PostMapping("/modificar")
    public String guardarAgentModificat(@Valid Agent agent,
            @RequestParam("imatgeDni") MultipartFile imatgeDniFile,
            @RequestParam("imatgeLlicencia") MultipartFile imatgeLlicenciaFile,
            Model model) {

        log.info("S'ha accedit al mètode guardarAgentModificat per l'agent amb DNI: {}", agent.getDni());
        Agent existent = agentService.obtenirAgentPerDni(agent.getDni());
        agent.setRol(existent.getRol());
        log.debug("Assignat rol existent a l'agent: {}", existent.getRol());

        if (agent.getNacionalitat() == null || agent.getNacionalitat().isEmpty()) {
            agent.setNacionalitat(existent.getNacionalitat());
            log.debug("Assignada nacionalitat existent a l'agent: {}", existent.getNacionalitat());
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusYears(50);

        if (agent.getDniCaducitat().isBefore(currentDate) || agent.getDniCaducitat().isAfter(maxDate)) {
            log.warn("La data d'expiració del DNI no és vàlida per l'agent amb DNI: {}", agent.getDni());
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("agent", agent);
            return "agent-modificar";
        }

        if (agent.getLlicCaducitat().isBefore(currentDate) || agent.getLlicCaducitat().isAfter(maxDate)) {
            log.warn("La data d'expiració de la llicència no és vàlida per l'agent amb DNI: {}", agent.getDni());
            model.addAttribute("error", "La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("agent", agent);
            return "agent-modificar";
        }

        if (agent.getContrasenya() == null || agent.getContrasenya().isEmpty()) {
            agent.setContrasenya(existent.getContrasenya());
            log.debug("S'ha reutilitzat la contrasenya existent per l'agent amb DNI: {}", agent.getDni());
        } else {
            // Si se ha proporcionado una nueva contraseña, encriptarla
            String contrasenyaEncriptada = passwordEncoder.encode(agent.getContrasenya());
            agent.setContrasenya(contrasenyaEncriptada);
            log.info("S'ha encriptat i assignat una nova contrasenya per l'agent amb DNI: {}", agent.getDni());
        }

        try {
            agentService.modificarAgent(agent);
            log.info("Agent modificat correctament amb DNI: {}", agent.getDni());

            // Recuperar o crear documentación en MongoDB
            DocumentacioClient documentacio = documentacioClientRepository.findById(agent.getDni())
                    .orElse(new DocumentacioClient());
            documentacio.setDni(agent.getDni());

            if (!imatgeDniFile.isEmpty()) {
                documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDniFile.getBytes()) });
                log.info("Assignada nova imatge DNI per l'agent amb DNI: {}", agent.getDni());
            }
            if (!imatgeLlicenciaFile.isEmpty()) {
                documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicenciaFile.getBytes()) });
                log.info("Assignada nova imatge llicència per l'agent amb DNI: {}", agent.getDni());
            }
            documentacioClientRepository.save(documentacio);
            log.info("Documentació actualitzada correctament per l'agent amb DNI: {}", agent.getDni());
        } catch (Exception e) {
            log.error("Error en modificar l'agent: {}", e.getMessage(), e);
            String error = e.getMessage();
            model.addAttribute("error", error);
            model.addAttribute("agent", agent);
            return "agent-modificar";
        }

        return "redirect:/agent/llistar";
    }

    /**
     * Elimina un agent pel seu DNI.
     *
     * @param dni   El DNI de l'agent a eliminar.
     * @param model El model per gestionar errors en cas que no es pugui eliminar.
     * @param agent L'agent que es vol eliminar.
     * @return Redirigeix a la llista d'agents després d'eliminar.
     */
    @GetMapping("/esborrar/{dni}")
    public String eliminarAgent(@PathVariable("dni") String dni, Principal principal,
            RedirectAttributes redirectAttributes, Agent agent) {
        log.info("S'ha accedit al mètode eliminarAgent per l'agent amb DNI: {}", dni);
        String usuariActual = principal.getName();
        Optional<Agent> opt = agentRepository.findById(dni);

        if (opt.isPresent()) {
            if (opt.get().getUsuari().equals(usuariActual)) {
                redirectAttributes.addFlashAttribute("error", "No pots eliminar el teu propi usuari.");
                return "redirect:/agent/llistar";
            }
        }
        try {
            agentService.eliminarAgent(agent); // Llama al servicio para eliminar
            log.info("Agent eliminat correctament amb DNI: {}", dni);
            redirectAttributes.addFlashAttribute("success", "Agent eliminat correctament.");
        } catch (RuntimeException e) {
            log.error("No s'ha pogut eliminar l'agent amb DNI: {}", dni, e);
            redirectAttributes.addFlashAttribute("error", "No s'ha pogut eliminar l'agent.");
            return "redirect:/agent/llistar";// Vuelve a la lista con un mensaje de error
        }

        return "redirect:/agent/llistar"; // Redirige al listado después de eliminar
    }

    /**
     * Consulta i mostra els detalls d'un agent pel seu DNI.
     *
     * @param dni   El DNI de l'agent a consultar.
     * @param model El model per passar les dades a la vista.
     * @return El nom de la plantilla per mostrar els detalls.
     */
    @GetMapping("/consulta/{dni}")
    public String mostrarDetallAgent(@PathVariable("dni") String dni, Model model) {

        log.info("S'ha accedit al mètode mostrarDetallAgent per l'agent amb DNI: {}", dni);
        Agent agent = agentService.obtenirAgentPerDni(dni); // Busca l'agent pel seu DNI
        if (agent == null) {
            log.error("No s'ha trobat cap agent amb el DNI especificat: {}", dni);
            throw new RuntimeException("No s'ha trobat cap agent amb el DNI especificat.");
        }
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);
        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
                log.debug("S'ha recuperat la imatge del DNI per l'agent amb DNI: {}", dni);
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
                log.debug("S'ha recuperat la imatge de la llicència per l'agent amb DNI: {}", dni);
            }
        }

        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);
        model.addAttribute("agent", agent);
        log.info("S'han carregat els detalls de l'agent amb DNI: {}", dni);
        return "agent-consulta"; // Nom de la plantilla
    }
}
