package com.pablogb.psychologger.scheduler;

import com.pablogb.psychologger.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class SnapshotScheduler {

    private final SnapshotService snapshotService;

    // runs at midnight on the 1st of every month
    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlySnapshot() {
        log.info("Running monthly snapshot job...");
        snapshotService.takeCurrentMonthSnapshot();
    }

    // runs on app startup - catches up any missing snapshots
    @EventListener(ApplicationReadyEvent.class)
    public void catchUpOnStartup() {
        log.info("Checking for missing snapshots on startup...");
        snapshotService.catchUpMissingSnapshots();
    }
}