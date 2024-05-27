package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.dto.PatientDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<PatientEntity, PatientView> patientViewMapper;

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
    public String createPatient(@Valid @ModelAttribute("patient") PatientEntity patient, BindingResult result, Model model) {
        model.addAttribute("patient", patient);
        if (result.hasErrors()) {
            return "addPatient";
        }
        patientService.savePatient(patient);
//        return "redirect:/view/patients/list";
        return "redirect:/";
    }

    @GetMapping("/view/sessions")
    public String getSessionForm(Model model,
                                 @RequestParam(required = false) Long id) {
        SessionEntity session = (id == null) ? new SessionEntity() : sessionService.getSession(id);
        model.addAttribute("session", session);
        return "addSession";
    }

    @PostMapping("/view/sessions")
    public String createSession(@ModelAttribute SessionEntity session) {
        sessionService.saveSession(session);
        return "index";
    }
}
