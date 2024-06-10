package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.controller.view.dto.*;
import com.pablogb.psychologger.domain.dto.PatchSessionDto;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<PatientEntity, PatientView> patientViewMapper;
    private final Mapper<SessionEntity, SessionCreateView> sessionViewMapper;
    private final Mapper<SessionEntity, SessionEditView> sessionEditViewMapper;

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
        Set<PatientListView> patients = patientService.getPatients().stream()
                .map(PatientListView::create)
                .collect(Collectors.toSet());
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/view/patients/{id}/sessions")
    public String getPatientSessions(@PathVariable Long id, Model model) {
        Set<SessionCreateView> patientSessions = patientService.getPatientSessions(id)
                .stream().map(sessionViewMapper::mapTo)
                .collect(Collectors.toSet());
        model.addAttribute("patientSessions", patientSessions);
        return "patientSessions";
    }

    @GetMapping("/view/sessions")
    public String getSessionForm(Model model,
                                 @RequestParam(required = false) Long id) {
        if (Objects.isNull(id)) {
            SessionCreateView sessionCreateView = new SessionCreateView();
            Set<PatientView> activePatients = patientService.getActivePatients().stream().map(patientViewMapper::mapTo).collect(Collectors.toSet());
            model.addAttribute("activePatients", activePatients);
            model.addAttribute("formMethod", "post");
            model.addAttribute("sessionView", sessionCreateView);
        } else {
            SessionEntity session = sessionService.getSession(id);
            List<PatientShort> patients = session.getPatients().stream().map(PatientShort::create).toList();
            SessionEditView sessionEditView = sessionEditViewMapper.mapTo(session);
            sessionEditView.setPatients(patients);
            model.addAttribute("activePatients", Collections.emptySet());
            model.addAttribute("formMethod", "put");
            model.addAttribute("sessionView", sessionEditView);
        }
        return "addSession";
    }

    @PutMapping("/view/sessions")
    public String updateSession(@Valid @ModelAttribute("sessionView") SessionCreateView sessionCreateView,
                                Model model) {
        model.addAttribute("sessionView", sessionCreateView);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        sessionService.partialUpdateSession(PatchSessionDto.builder()
                .id(sessionCreateView.getId())
                .nextWeek(sessionCreateView.getNextWeek())
                .content(sessionCreateView.getContent())
                .isPaid(sessionCreateView.getIsPaid())
                .isImportant(sessionCreateView.getIsImportant())
                .subject(sessionCreateView.getSubject())
                .sessionDate(LocalDate.parse(sessionCreateView.getSessionDate(), format))
                .build());
        return "redirect:/view/sessions/list";
    }

    @PostMapping("/view/sessions")
    public String createSession(@Valid @ModelAttribute("sessionView") SessionCreateView sessionCreateView, BindingResult result, Model model) {
        model.addAttribute("sessionView", sessionCreateView);
        if (result.hasErrors()) {
            return "addSession";
        }

        List<Long> patientIds = getPatientIds(sessionCreateView.getPatients());

        Set<PatientEntity> patients = new HashSet<>();
        for (Long id : patientIds) {
            PatientEntity patient = patientService.getPatient(id);
            patients.add(patient);
        }

        SessionEntity session = sessionViewMapper.mapFrom(sessionCreateView);
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
