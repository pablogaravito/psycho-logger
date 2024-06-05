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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        SessionEntity session = (id == null) ? new SessionEntity() : sessionService.getSession(id);
//        SessionView sessionView = sessionViewMapper.mapTo(session);

        Set<PatientView> activePatients = patientService.getActivePatients().stream().map(patientViewMapper::mapTo).collect(Collectors.toSet());
        model.addAttribute("sessionView", new SessionView());
        model.addAttribute("activePatients", activePatients);
        return "addSession";
    }

    @PostMapping("/view/sessions")
    public String createSession(@Valid @ModelAttribute("sessionView") SessionView sessionView, BindingResult result, Model model) {
        model.addAttribute("sessionView", sessionView);
        if (result.hasErrors()) {
            return "addSession";
        }
        List<Long> patientIds = getPatientIds(sessionView.getPatients());

        Set<PatientEntity> patients = new HashSet<>();
        for (Long id : patientIds) {
            PatientEntity patient = patientService.getPatient(id);
            patients.add(patient);
        }

        SessionEntity session = sessionViewMapper.mapFrom(sessionView);
        session.setPatients(patients);
        sessionService.saveSession(session);
        return "redirect:/view/sessions/list";
    }

    @GetMapping("/view/sessions/list")
    public String getSessionsList(Model model) {
        Set<SessionEntity> sessions = sessionService.getSessions();

        Set<SessionListView> sessionListViews = sessions.stream().map(s -> SessionListView.create(s, patientService::retrievePatients)).collect(Collectors.toSet());
        model.addAttribute("sessionViews", sessionListViews);
        return "sessions";
    }

    private List<Long> getPatientIds(String csvInput) {
        return Stream.of(csvInput.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
    }
}
