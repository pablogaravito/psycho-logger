package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.dto.api.PatchSessionDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.*;
import com.pablogb.psychologger.model.entity.PatientEntity;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/sessions")
public class SessionViewController {

    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<PatientDto, PatientView> patientViewMapper;
    private final Mapper<SessionEntity, SessionCreateView> sessionViewMapper;
    private final Mapper<SessionEntity, SessionEditView> sessionEditViewMapper;

    @GetMapping
    public String getSessionForm(Model model,
                                 @RequestParam(required = false) Long id) {
        if (Objects.isNull(id)) {
            SessionCreateView sessionCreateView = new SessionCreateView();
            List<PatientView> activePatients = patientService.getActivePatients().stream().map(patientViewMapper::mapTo).toList();
            model.addAttribute("activePatients", activePatients);
            model.addAttribute("formMethod", "post");
            model.addAttribute("sessionView", sessionCreateView);
        } else {
            SessionEntity session = sessionService.getSession(id);
            List<PatientShort> patients = session.getPatients().stream().map(PatientShort::create).toList();
            SessionEditView sessionEditView = sessionEditViewMapper.mapTo(session);
            sessionEditView.setPatients(patients);
            model.addAttribute("activePatients", Collections.emptyList());
            model.addAttribute("formMethod", "put");
            model.addAttribute("sessionView", sessionEditView);
        }
        return "addSession";
    }

    @PutMapping
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
                .themes(sessionCreateView.getThemes())
                .sessionDate(LocalDate.parse(sessionCreateView.getSessionDate(), format))
                .build());
        return "redirect:/view/sessions/list";
    }

    @PostMapping
    public String createSession(@Valid @ModelAttribute("sessionView") SessionCreateView sessionCreateView, BindingResult result, Model model) {
        model.addAttribute("sessionView", sessionCreateView);
        if (result.hasErrors()) {
            return "addSession";
        }

        List<Long> patientIds = getPatientIds(sessionCreateView.getPatients());

//        List<PatientEntity> patients = new ArrayList<>();
//        for (Long id : patientIds) {
//            PatientEntity patient = patientService.getPatient(id);
//            patients.add(patient);
//        }

        SessionEntity session = sessionViewMapper.mapFrom(sessionCreateView);
//        session.setPatients(patients);
        sessionService.saveSession(session);

        return "redirect:/view/sessions/list";
    }

    @GetMapping("/list")
    public String getSessionsPage(Model model,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "4") int size) {
        Page<SessionEntity> sessionsPage;
        if (keyword == null) {
            sessionsPage = sessionService.getSessionsPaginated(page, size);
        } else {
            sessionsPage = sessionService.getSessionsPaginated(keyword, page, size);
            model.addAttribute("keyword", keyword);
        }

        Page<SessionListView> sessionViews = sessionsPage.map(s -> SessionListView.create(s, patientService::retrievePatients));

        model.addAttribute("sessionViews", sessionViews.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessionViews.getTotalPages());
        model.addAttribute("totalItems", sessionViews.getTotalElements());
        return "sessions";
    }

    private List<Long> getPatientIds(String csvInput) {
        return Stream.of(csvInput.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
    }
}
