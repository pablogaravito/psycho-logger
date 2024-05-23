package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.domain.dto.PatchPatientDto;
import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class ViewController {

    private final PatientService patientService;

    @GetMapping("/")
    public String startPage() {
        return "index";
    }

    @GetMapping("/view/patients")
    public String getPatientForm(Model model,
                                 @RequestParam(required = false) Long id) {
        PatientEntity patient = (id == null) ? new PatientEntity() : patientService.getPatient(id);
        model.addAttribute("patient", patient);
        return "addPatient";
    }

    @PostMapping("/view/patients")
    public String createPatient(@ModelAttribute PatientEntity patient) {
        patientService.savePatient(patient);
        return "index";
    }

}
