package com.pablogb.psychologger.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/search")
public class SearchViewController {

    @GetMapping("/{name}")
    public String searchPatient(@PathVariable Long id, Model model) {

        return "patientSearch";
    }

    @GetMapping("/birthday")
    public String getBirthdayBoys(Model model) {

        return "birthday";
    }

    @GetMapping("/debt")
    public String getDebtBoys(Model model) {

        return "debt";
    }

}
