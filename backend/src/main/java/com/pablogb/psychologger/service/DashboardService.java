package com.pablogb.psychologger.service;

import com.pablogb.psychologger.dto.response.StatsResponseDto;

public interface DashboardService {
    StatsResponseDto getStats();
}
