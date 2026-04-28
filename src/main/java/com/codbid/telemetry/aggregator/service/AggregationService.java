package com.codbid.telemetry.aggregator.service;

import com.codbid.telemetry.aggregator.config.AggregationProperties;
import com.codbid.telemetry.aggregator.model.aggregation.AggregationKey;
import com.codbid.telemetry.aggregator.model.aggregation.WindowState;
import com.codbid.telemetry.aggregator.model.event.TelemetryEvent;
import com.codbid.telemetry.aggregator.store.WindowStateStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AggregationService {

    private final AggregationProperties aggregationProperties;
    private final WindowCalculator windowCalculator;
    private final WindowStateStore windowStateStore;

    public AggregationService(
            AggregationProperties aggregationProperties,
            WindowCalculator windowCalculator,
            WindowStateStore windowStateStore
    ) {
        this.aggregationProperties = aggregationProperties;
        this.windowCalculator = windowCalculator;
        this.windowStateStore = windowStateStore;
    }

    public void process(TelemetryEvent event) {
        validateEvent(event);


    }

    public void processWindow(TelemetryEvent event, AggregationProperties.WindowProperties window) {
        Instant windowStart = windowCalculator.calculateWindowStart(event.timestamp(), window.getDuration());
        Instant windowEnd = windowCalculator.calculateWindowEnd(windowStart, window.getDuration());

        AggregationKey key = new AggregationKey(
                window.getName(),
                windowStart,
                event.service(),
                event.operation(),
                event.kind(),
                event.environment()
        );

        WindowState state = windowStateStore.getOrCreate(key, () -> new WindowState(windowStart, windowEnd));

        state.addEvent(event);
    }

    private void validateEvent(TelemetryEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Telemetry event must not be null");
        }

        if (event.timestamp() == null) {
            throw new IllegalArgumentException("Telemetry event timestamp must not be null");
        }

        if (event.service() == null) {
            throw new IllegalArgumentException("Telemetry event service must not be null");
        }

        if (event.operation() == null) {
            throw new IllegalArgumentException("Telemetry event operation must not be null");
        }

        if (event.kind() == null) {
            throw new IllegalArgumentException("Telemetry event kind must not be null");
        }

        if (event.status() == null) {
            throw new IllegalArgumentException("Telemetry event status must not be null");
        }
    }
}
