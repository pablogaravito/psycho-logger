package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.dto.api.CreatePatientDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.PatientListView;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.view.PatientView;
import com.pablogb.psychologger.dto.view.SessionListViewShort;
import com.pablogb.psychologger.model.entity.SessionEntity;
import com.pablogb.psychologger.mapper.Mapper;
import com.pablogb.psychologger.service.PatientService;
import com.pablogb.psychologger.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/patients")
public class PatientViewController {
    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<CreatePatientDto, PatientView> patientCreateViewMapper;
    private final Mapper<PatientDto, PatientView> patientViewMapper;

    @GetMapping
    public String getPatientForm(Model model,
                                 @RequestParam(required = false) Long id) {
//        PatientEntity patient = (id == null) ? new PatientEntity() : patientService.getPatient(id);
//        PatientView patientView = patientViewMapper.mapTo(patient);
//        model.addAttribute("patient", patientView);
        PatientDto patient = (id == null) ? new PatientDto() : patientService.getPatient(id);
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
//        PatientEntity patient = patientViewMapper.mapFrom(patientView);
        CreatePatientDto patient = patientCreateViewMapper.mapFrom(patientView);
        patientService.savePatient(patient);
        return "redirect:/view/patients/list";
    }

    @GetMapping("/list")
    public String getPatientsPage(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {

//        Page<PatientEntity> patientsPage = patientService.getPatientsPaginated(page, size);
        Page<PatientDto> patientsPage = patientService.getPatientsPaginated(page, size);
        Page<PatientListView> patients = patientsPage.map(PatientListView::createFromDto);

        model.addAttribute("patients", patients.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patients.getTotalPages());
        model.addAttribute("totalItems", patients.getTotalElements());
        return "patients";
    }

    @GetMapping("/{id}/sessions")
    public String getPatientSessions(Model model,
                                     @PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "4") int size) {
        Page<SessionEntity> sessionsPage = sessionService.getPatientSessionsPaginated(id, page, size);
        Page<SessionListViewShort> patientSessions = sessionsPage.map(SessionListViewShort::create);
        PatientShort patient = PatientShort.createFromDto(patientService.getPatient(id));
        model.addAttribute("patient", patient);
        model.addAttribute("patientSessions", patientSessions.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientSessions.getTotalPages());
        model.addAttribute("totalItems", patientSessions.getTotalElements());
        return "patientSessions";
    }
}
