package com.pablogb.psychologger.controller.gui;

import com.pablogb.psychologger.controller.gui.view.PatientListView;
import com.pablogb.psychologger.domain.dto.PatientWithBirthdayContextDto;
import com.pablogb.psychologger.domain.dto.PatientWithDebtContextDto;
import com.pablogb.psychologger.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/search")
public class SearchViewController {

    private final PatientService patientService;

    @GetMapping
    public String searchPatient(@RequestParam(name = "keyword") String keyword, Model model) {
        List<PatientListView> patients = patientService.searchPatientByName(keyword).stream()
                .map(PatientListView::create)
                .toList();
        model.addAttribute("keyword", keyword);
        model.addAttribute("patients", patients);
        return "patientSearch";
    }

    @GetMapping("/birthday")
    public String getBirthdayPeople(Model model) {
        List<PatientWithBirthdayContextDto> birthdayBoys = patientService.getPatientsWithIncomingBirthdays();
        model.addAttribute("birthdayBoys", birthdayBoys);
        return "birthday";
    }

    @GetMapping("/debt")
    public String getDebtPeople(Model model) {
        List<PatientWithDebtContextDto> patientsWithDebt = patientService.getPatientsWithDebt();
        model.addAttribute("patients", patientsWithDebt);
        return "debt";
    }
}
