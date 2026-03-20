package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.response.AssignmentResponseDto;
import com.pablogb.psychologger.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssignmentResponseDto>> getAllActiveAssignments() {
        return ResponseEntity.ok(assignmentService.getAllActiveAssignments());
    }
}
