package com.codbid.telemetry.aggregator.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelemetryEvent(
        String eventId,
        Instant timestamp,

        String service,
        String environment,
        String instanceId,

        String operation,
        String component,
        String method,

        TelemetryKind kind,
        TelemetryStatus status,

        Long durationMs,

        String errorType,
        String errorCode,

        String traceId,
        String spanId,
        String correlationId,

        Map<String, String> tags
) {
}
