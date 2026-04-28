package com.codbid.telemetry.aggregator.store;

import com.codbid.telemetry.aggregator.model.aggregation.AggregationKey;
import com.codbid.telemetry.aggregator.model.aggregation.WindowState;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

public interface WindowStateStore {

    WindowState getOrCreate(AggregationKey key, Supplier<WindowState> windowStateSupplier);

    List<Entry> findAll();

    void deleteExpired(Instant now);

    record Entry(AggregationKey key, WindowState state) {}
}
