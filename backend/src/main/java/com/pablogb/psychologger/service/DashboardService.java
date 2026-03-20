package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.response.MonthlySnapshotDto;
import com.pablogb.psychologger.dto.response.StatsResponseDto;

import java.util.List;

public interface DashboardService {
    StatsResponseDto getStats();
    List<MonthlySnapshotDto> getSnapshots();
}
