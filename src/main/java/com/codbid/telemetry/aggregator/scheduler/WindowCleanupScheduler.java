package com.codbid.telemetry.aggregator.scheduler;

import com.codbid.telemetry.aggregator.store.WindowStateStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WindowCleanupScheduler {
    private WindowStateStore store;

    public WindowCleanupScheduler(WindowStateStore store) {
        this.store = store;
    }

    @Scheduled(fixedDelayString = "${telemetry.aggregation.cleanup-interval}")
    public void cleanup() {
        store.deleteExpired(Instant.now());
    }
}
