package com.pablogb.psychologger.controller;

import com.pablogb.psychologger.dto.response.MonthlySnapshotDto;
import com.pablogb.psychologger.dto.response.StatsResponseDto;
import com.pablogb.psychologger.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDto> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<MonthlySnapshotDto>> getSnapshots() {
        return ResponseEntity.ok(dashboardService.getSnapshots());
    }
}