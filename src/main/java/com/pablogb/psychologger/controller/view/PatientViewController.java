package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.dto.api.PatientCreationDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.view.PatientListView;
import com.pablogb.psychologger.dto.view.PatientShort;
import com.pablogb.psychologger.dto.view.SessionListViewShort;
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
    private final Mapper<PatientCreationDto, PatientDto> patientCreationDtoToPatientDtoMapper;

    @GetMapping
    public String getPatientForm(Model model,
                                 @RequestParam(required = false) Long id) {
        PatientDto patient = (id == null) ? new PatientDto() : patientService.getPatient(id);
        PatientCreationDto patientView = patientCreationDtoToPatientDtoMapper.mapFrom(patient);
        model.addAttribute("patient", patientView);
        return "addPatient";
    }

    @PostMapping
    public String createPatient(@Valid @ModelAttribute("patient") PatientCreationDto patientView, BindingResult result, Model model) {
        model.addAttribute("patient", patientView);
        if (result.hasErrors()) {
            return "addPatient";
        }
        patientService.savePatient(patientView);
        return "redirect:/view/patients/list";
    }

    @GetMapping("/list")
    public String getPatientsPage(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {
        Page<PatientListView> patients = patientService.getPatientsPaginated(page, size).map(PatientListView::createFromDto);

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
        Page<SessionDto> sessionsPage = sessionService.getPatientSessionsPaginated(id, page, size);
        Page<SessionListViewShort> patientSessions = sessionsPage.map(SessionListViewShort::createFromDto);
        PatientShort patient = PatientShort.createFromDto(patientService.getPatient(id));

        model.addAttribute("patient", patient);
        model.addAttribute("patientSessions", patientSessions.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientSessions.getTotalPages());
        model.addAttribute("totalItems", patientSessions.getTotalElements());
        return "patientSessions";
    }
}
