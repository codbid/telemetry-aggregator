package com.codbid.telemetry.aggregator.model.aggregation;

import com.codbid.telemetry.aggregator.model.event.TelemetryKind;

import java.time.Instant;

public record AggregatedMetric (
        String windowName,
        Instant windowStart,
        Instant windowEnd,

        String service,
        String operation,
        TelemetryKind kind,
        String environment,

        long requestsTotal,
        long successTotal,
        long errorsTotal,

        double errorRate,
        double successRate,

        double latencyAvgMs,
        Long latencyMinMs,
        Long latencyMaxMs,
        double latencyP95Ms,
        double latencyP99Ms,

        double rps
) {
}