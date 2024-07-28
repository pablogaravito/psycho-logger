package com.pablogb.psychologger.controller.view;

import com.pablogb.psychologger.dto.api.SessionCreationDto;
import com.pablogb.psychologger.dto.api.SessionDto;
import com.pablogb.psychologger.dto.api.PatientDto;
import com.pablogb.psychologger.dto.view.*;
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

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/sessions")
public class SessionViewController {

    private final PatientService patientService;
    private final SessionService sessionService;
    private final Mapper<PatientDto, PatientShort> patientDtoToPatientShortMapper;
    private final Mapper<SessionEntity, SessionCreateView> sessionViewMapper;
            private final Mapper<SessionEntity, SessionDto> sessionDtoMapper;

    @GetMapping
    public String getSessionForm(Model model,
                                 @RequestParam(required = false) Long id) {
        if (Objects.isNull(id)) {
            SessionCreateView sessionCreateView = new SessionCreateView();
            List<PatientShort> activePatients = patientService.getActivePatients().stream().map(PatientShort::createFromDto).toList();

            model.addAttribute("activePatients", activePatients);
            model.addAttribute("formMethod", "post");
            model.addAttribute("sessionView", sessionCreateView);
        } else {
            SessionDto sessionDto = sessionService.getSession(id);
            List<PatientShort> patients = sessionDto.getPatients().stream().map(PatientShort::createFromDto).toList();
            SessionEditView sessionEditView = SessionEditView.createFromDto(sessionDto);
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
        // fix THIS ***
//        sessionService.partialUpdateSession(SessionDto.builder()
//                .id(sessionCreateView.getId())
//                .nextWeek(sessionCreateView.getNextWeek())
//                .content(sessionCreateView.getContent())
//                .isPaid(sessionCreateView.getIsPaid())
//                .isImportant(sessionCreateView.getIsImportant())
//                .themes(sessionCreateView.getThemes())
//                .sessionDate(LocalDate.parse(sessionCreateView.getSessionDate(), format))
//                .build());
        return "redirect:/view/sessions/list";
    }

    @PostMapping
    public String createSession(@Valid @ModelAttribute("sessionView") SessionCreateView sessionCreateView, BindingResult result, Model model) {
        model.addAttribute("sessionView", sessionCreateView);
        if (result.hasErrors()) {
            return "addSession";
        }

        SessionCreationDto sessionCreationDto = SessionCreationDto.createFromSessionCreateView(sessionCreateView);

//        SessionEntity session = sessionViewMapper.mapFrom(sessionCreateView);
//        session.setPatients(patients);
//        sessionService.saveSession(sessionDtoMapper.mapTo(session));
        sessionService.saveSession(sessionCreationDto);
        return "redirect:/view/sessions/list";
    }

    @GetMapping("/list")
    public String getSessionsPage(Model model,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "4") int size) {
        Page<SessionDto> sessionsPage;

        if (keyword == null) {
            sessionsPage = sessionService.getSessionsPaginated(page, size);
        } else {
            sessionsPage = sessionService.getSessionsPaginated(keyword, page, size);
            model.addAttribute("keyword", keyword);
        }

        Page<SessionListView> sessionViews = sessionsPage.map(s -> SessionListView.createFromDto(s, patientService::retrievePatients));

        model.addAttribute("sessionViews", sessionViews.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sessionViews.getTotalPages());
        model.addAttribute("totalItems", sessionViews.getTotalElements());
        return "sessions";
    }
}
