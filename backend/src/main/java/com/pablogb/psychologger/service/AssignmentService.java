package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.response.AssignmentResponseDto;
import java.util.List;

public interface AssignmentService {
    List<AssignmentResponseDto> getAllActiveAssignments();
}
