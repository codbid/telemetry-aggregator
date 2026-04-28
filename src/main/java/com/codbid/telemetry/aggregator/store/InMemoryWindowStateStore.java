package com.codbid.telemetry.aggregator.store;

import com.codbid.telemetry.aggregator.config.AggregationProperties;
import com.codbid.telemetry.aggregator.model.aggregation.AggregationKey;
import com.codbid.telemetry.aggregator.model.aggregation.WindowState;
import com.codbid.telemetry.aggregator.service.WindowCalculator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class InMemoryWindowStateStore implements WindowStateStore{
    private final Map<AggregationKey, WindowState> storage = new ConcurrentHashMap<>();

    private final AggregationProperties aggregationProperties;
    private final WindowCalculator windowCalculator;

    public InMemoryWindowStateStore(AggregationProperties aggregationProperties, WindowCalculator windowCalculator) {
        this.aggregationProperties = aggregationProperties;
        this.windowCalculator = windowCalculator;
    }

    @Override
    public WindowState getOrCreate(AggregationKey key, Supplier<WindowState> windowStateSupplier) {
        return storage.computeIfAbsent(key, ignored -> windowStateSupplier.get());
    }

    @Override
    public List<Entry> findAll() {
        return storage.entrySet().stream()
                .map(entry ->
                        new Entry(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public void deleteExpired(Instant now) {
        storage.entrySet().removeIf( entry -> {
            AggregationKey key = entry.getKey();
            WindowState state = entry.getValue();

            Duration retention = findRetentionByWindowName(key.windowName());

            return windowCalculator.isExpired(state.getWindowStart(), retention, now);
        });
    }

    private Duration findRetentionByWindowName(String windowName) {
        return aggregationProperties.getWindows().stream()
                .filter(window -> window.getName().equals(windowName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aggregation window config not found: " + windowName))
                .getRetention();
    }
}
