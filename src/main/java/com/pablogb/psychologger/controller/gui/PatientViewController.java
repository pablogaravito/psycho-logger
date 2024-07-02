package com.pablogb.psychologger.controller.gui;

import com.pablogb.psychologger.controller.gui.view.*;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/patients")
public class PatientViewController {
    private final PatientService patientService;
    private final Mapper<PatientEntity, PatientView> patientViewMapper;
    private final Mapper<SessionEntity, SessionCreateView> sessionViewMapper;

    @GetMapping
    public String getPatientForm(Model model,
                                 @RequestParam(required = false) Long id) {
        PatientEntity patient = (id == null) ? new PatientEntity() : patientService.getPatient(id);
        PatientView patientView = patientViewMapper.mapTo(patient);
        model.addAttribute("patient", patientView);
        return "addPatient";
    }

    @PostMapping
    public String createPatient(@Valid @ModelAttribute("patient") PatientView patientView, BindingResult result, Model model) {
        model.addAttribute("patient", patientView);
        if (result.hasErrors()) {
            return "addPatient";
        }
        PatientEntity patient = patientViewMapper.mapFrom(patientView);
        patientService.savePatient(patient);
        return "redirect:/view/patients/list";
    }

    @GetMapping("/old")
    public String getPatientsList(Model model) {
        Set<PatientListView> patients = patientService.getPatients().stream()
                .map(PatientListView::create)
                .collect(Collectors.toSet());
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/list")
    public String getPatientsPage(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {

        Page<PatientEntity> patientsPage = patientService.getPatientsPaginated(page, size);
        Page<PatientListView> patients = patientsPage.map(PatientListView::create);

        model.addAttribute("patients", patients.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patients.getTotalPages());
        model.addAttribute("totalItems", patients.getTotalElements());
        return "patients";
    }


    @GetMapping("/{id}/sessions")
    public String getPatientSessions(@PathVariable Long id, Model model) {
        List<SessionListViewShort> patientSessions = patientService.getPatientSessions(id)
                .stream().map(SessionListViewShort::create).toList();
        PatientShort patient = PatientShort.create(patientService.getPatient(id));
        model.addAttribute("patient", patient);
        model.addAttribute("patientSessions", patientSessions);
        return "patientSessions";
    }

    @GetMapping("/{id}/debt")
    public String getPatientDebt(@PathVariable Long id, Model model) {
        Set<SessionCreateView> patientSessions = patientService.getPatientSessions(id)
                .stream().map(sessionViewMapper::mapTo)
                .collect(Collectors.toSet());
        PatientShort patient = PatientShort.create(patientService.getPatient(id));
        model.addAttribute("patient", patient);
        model.addAttribute("patientSessions", patientSessions);
        return "patientDebt";
    }
}
