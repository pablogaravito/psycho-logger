package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<PatientEntity, PatientView> patientViewMapper;
    private final Mapper<SessionEntity, SessionView> sessionViewMapper;

    @GetMapping("/")
    public String startPage() {
        return "start";
    }

    @GetMapping("/view/patients")
    public String getPatientForm(Model model,
                                 @RequestParam(required = false) Long id) {
        PatientEntity patient = (id == null) ? new PatientEntity() : patientService.getPatient(id);
        PatientView patientView = patientViewMapper.mapTo(patient);
        model.addAttribute("patient", patientView);
        return "addPatient";
    }

    @PostMapping("/view/patients")
    public String createPatient(@Valid @ModelAttribute("patient") PatientView patientView, BindingResult result, Model model) {
        model.addAttribute("patient", patientView);
        if (result.hasErrors()) {
            return "addPatient";
        }
        PatientEntity patient = patientViewMapper.mapFrom(patientView);
        patientService.savePatient(patient);
        return "redirect:/view/patients/list";
    }

    @GetMapping("/view/patients/list")
    public String getPatientsList(Model model) {
        Set<PatientView> patients = patientService.getPatients().stream()
                .map(patientViewMapper::mapTo)
                .collect(Collectors.toSet());
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/view/patients/{id}/sessions")
    public String getPatientSessions(@PathVariable Long id, Model model) {
        Set<SessionView> patientSessions = patientService.getPatientSessions(id)
                .stream().map(sessionViewMapper::mapTo)
                .collect(Collectors.toSet());
        model.addAttribute("patientSessions", patientSessions);
        return "patientSessions";
    }

    @GetMapping("/view/sessions")
    public String getSessionForm(Model model,
                                 @RequestParam(required = false) Long id) {
        SessionEntity session = (id == null) ? new SessionEntity() : sessionService.getSession(id);
        model.addAttribute("session", session);
        return "addSession";
    }

    @PostMapping("/view/sessions")
    public String createSession(@Valid @ModelAttribute("session") SessionView sessionView, BindingResult result, Model model) {
        model.addAttribute("session", sessionView);
        if (result.hasErrors()) {
            return "addSession";
        }
        SessionEntity session = sessionViewMapper.mapFrom(sessionView);
        sessionService.saveSession(session);
        return "redirect:/view/sessions/list";
    }

    @GetMapping("/view/sessions/list")
    public String getSessionsList(Model model) {
        Set<SessionView> sessions = sessionService.getSessions().stream()
                .map(sessionViewMapper::mapTo)
                .collect(Collectors.toSet());
        model.addAttribute("sessions", sessions);
        return "sessions";
    }


}
