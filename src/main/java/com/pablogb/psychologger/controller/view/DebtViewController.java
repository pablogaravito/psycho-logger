package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.dto.view.PatientWithDebtContextDto;
import com.pablogb.psychologger.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/view/debt")
public class DebtViewController {

    private final DebtService debtService;

    @GetMapping()
    public String getDebtPeople(Model model) {
        List<PatientWithDebtContextDto> patientsWithDebt = debtService.getPatientsWithDebt();
        model.addAttribute("patients", patientsWithDebt);
        return "debt";
    }

    @PostMapping("/pay")
    public String updateDebtSessions(@RequestParam(required = false) List<Long> sessionIds) {
        if (sessionIds != null) {
            debtService.updateSessionPaidStatus(sessionIds);
        }
        return "redirect:/view/debt";
    }
}
