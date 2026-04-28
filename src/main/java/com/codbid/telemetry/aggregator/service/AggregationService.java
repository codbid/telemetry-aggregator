package com.codbid.telemetry.aggregator.service;

import com.codbid.telemetry.aggregator.config.AggregationProperties;
import com.codbid.telemetry.aggregator.model.aggregation.AggregatedMetric;
import com.codbid.telemetry.aggregator.model.aggregation.AggregationKey;
import com.codbid.telemetry.aggregator.model.aggregation.WindowState;
import com.codbid.telemetry.aggregator.model.event.TelemetryEvent;
import com.codbid.telemetry.aggregator.store.WindowStateStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class AggregationService {

    private final AggregationProperties aggregationProperties;
    private final WindowCalculator windowCalculator;
    private final WindowStateStore windowStateStore;
    private final AggregatedMetricCalculator metricCalculator;

    public AggregationService(
            AggregationProperties aggregationProperties,
            WindowCalculator windowCalculator,
            WindowStateStore windowStateStore,
            AggregatedMetricCalculator metricCalculator
    ) {
        this.aggregationProperties = aggregationProperties;
        this.windowCalculator = windowCalculator;
        this.windowStateStore = windowStateStore;
        this.metricCalculator = metricCalculator;
    }

    public void process(TelemetryEvent event) {
        validateEvent(event);

        Instant now = Instant.now();

        for (AggregationProperties.WindowProperties window : aggregationProperties.getWindows()) {
            processWindow(event, window, now);
        }
    }

    public List<AggregatedMetric> findAllAggregates() {
        return windowStateStore.findAll().stream()
                .map(entry -> metricCalculator.calculate(entry.key(), entry.state()))
                .sorted(
                        Comparator
                                .comparing(AggregatedMetric::windowStart)
                                .reversed()
                                .thenComparing(AggregatedMetric::environment)
                                .thenComparing(AggregatedMetric::service)
                                .thenComparing(AggregatedMetric::operation)
                                .thenComparing(AggregatedMetric::windowName)
                                .thenComparing(metric -> metric.kind().name())
                ).toList();
    }

    public void processWindow(TelemetryEvent event, AggregationProperties.WindowProperties windowProperties, Instant now) {
        Instant windowStart = windowCalculator.calculateWindowStart(event.timestamp(), windowProperties.getDuration());
        Instant windowEnd = windowCalculator.calculateWindowEnd(windowStart, windowProperties.getDuration());

        if (windowCalculator.isExpired(windowEnd, windowProperties.getRetention(), now)) {
            return;
        }

        AggregationKey key = new AggregationKey(
                windowProperties.getName(),
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
