package com.codbid.telemetry.aggregator.model.aggregation;

import com.codbid.telemetry.aggregator.model.event.TelemetryEvent;
import com.codbid.telemetry.aggregator.model.event.TelemetryStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WindowState {

    private final Instant windowStart;
    private final Instant windowEnd;

    private long requestsTotal;
    private long successTotal;
    private long errorsTotal;

    private long latencyTotalMs;
    private Long latencyMinMs;
    private Long latencyMaxMs;

    private final List<Long> durationsMs = new ArrayList<>();

    public WindowState(Instant windowStart, Instant windowEnd) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public synchronized void addEvent(TelemetryEvent event) {
        requestsTotal++;

        if (event.status() == TelemetryStatus.ERROR) {
            errorsTotal++;
        } else {
            successTotal++;
        }

        Long durationMs = event.durationMs();

        if (durationMs != null && durationMs >= 0) {
            durationsMs.add(durationMs);
            latencyTotalMs += durationMs;

            if (latencyMinMs == null || durationMs < latencyMinMs)
                latencyMinMs = durationMs;

            if (latencyMaxMs == null || durationMs > latencyMaxMs)
                latencyMaxMs = durationMs;
        }
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public synchronized long getRequestsTotal() {
        return requestsTotal;
    }

    public synchronized long getSuccessTotal() {
        return successTotal;
    }

    public synchronized long getErrorsTotal() {
        return errorsTotal;
    }

    public synchronized long getLatencyTotalMs() {
        return latencyTotalMs;
    }

    public synchronized Long getLatencyMinMs() {
        return latencyMinMs;
    }

    public synchronized Long getLatencyMaxMs() {
        return latencyMaxMs;
    }

    public synchronized List<Long> getDurationsMsCopy() {
        return new ArrayList<>(durationsMs);
    }
}
