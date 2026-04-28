package com.codbid.telemetry.aggregator.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class WindowCalculator {
    public Instant calculateWindowStart(Instant eventTimestamp, Duration windowDuration) {
        long eventEpochMillis = eventTimestamp.toEpochMilli();
        long windowMillis = windowDuration.toMillis();

        long windowStartMillis = eventEpochMillis - (eventEpochMillis % windowMillis);

        return Instant.ofEpochMilli(windowStartMillis);
    }

    public Instant calculateWindowEnd(Instant windowStart, Duration windowDuration) {
        return windowStart.plus(windowDuration);
    }

    public boolean isExpired(Instant windowEnd, Duration retention, Instant now) {
        return windowEnd.plus(retention).isBefore(now);
    }
}
