package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.controller.view.dto.PatientListView;
import com.pablogb.psychologger.controller.view.dto.PatientView;
import com.pablogb.psychologger.controller.view.dto.SessionCreateView;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
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

    @GetMapping("/list")
    public String getPatientsList(Model model) {
        Set<PatientListView> patients = patientService.getPatients().stream()
                .map(PatientListView::create)
                .collect(Collectors.toSet());
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/{id}/sessions")
    public String getPatientSessions(@PathVariable Long id, Model model) {
        Set<SessionCreateView> patientSessions = patientService.getPatientSessions(id)
                .stream().map(sessionViewMapper::mapTo)
                .collect(Collectors.toSet());
        model.addAttribute("patientSessions", patientSessions);
        return "patientSessions";
    }
}
