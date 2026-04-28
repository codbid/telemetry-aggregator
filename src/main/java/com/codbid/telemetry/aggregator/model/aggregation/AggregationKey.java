package com.codbid.telemetry.aggregator.model.aggregation;

import com.codbid.telemetry.aggregator.model.event.TelemetryKind;

import java.time.Instant;

public record AggregationKey(
        String windowName,
        Instant windowStart,
        String service,
        String operation,
        TelemetryKind kind,
        String environment
) {
}
