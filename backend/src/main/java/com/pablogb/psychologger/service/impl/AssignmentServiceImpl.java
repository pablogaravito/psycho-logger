package com.pablogb.psychologger.service.impl;

import com.pablogb.psychologger.dto.response.AssignmentResponseDto;
import com.pablogb.psychologger.repository.TherapistPatientAssignmentRepository;
import com.pablogb.psychologger.security.SecurityUtils;
import com.pablogb.psychologger.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final TherapistPatientAssignmentRepository assignmentRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> getAllActiveAssignments() {
        Integer orgId = securityUtils.getCurrentOrgId();
        return assignmentRepository
                .findByPatientOrganizationId(orgId)
                .stream()
                .map(a -> AssignmentResponseDto.builder()
                        .id(a.getId())
                        .therapistId(a.getTherapist().getId())
                        .patientId(a.getPatient().getId())
                        .assignedAt(a.getAssignedAt())
                        .unassignedAt(a.getUnassignedAt())
                        .build())
                .toList();
    }
}
