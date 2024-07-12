package com.pablogb.psychologger.controller.api;

import com.pablogb.psychologger.dto.api.DebtSessionDto;
import com.pablogb.psychologger.dto.api.PatientWithDebtCountDto;
import com.pablogb.psychologger.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/debt")
public class DebtController {
    private final DebtService debtService;

    @GetMapping("/{id}")
    public ResponseEntity<List<DebtSessionDto>> getPatientDebt(@PathVariable Long id) {
        return new ResponseEntity<>(debtService.getPatientDebt(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PatientWithDebtCountDto>> getGeneralDebt() {
        return new ResponseEntity<>(debtService.getPatientsWithDebtApi(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> payDebt(@RequestBody List<Long> sessionIds) {
        if (sessionIds != null) {
            debtService.updateSessionPaidStatus(sessionIds);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
