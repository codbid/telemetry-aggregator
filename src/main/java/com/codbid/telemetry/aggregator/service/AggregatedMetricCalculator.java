package com.codbid.telemetry.aggregator.service;

import com.codbid.telemetry.aggregator.model.aggregation.AggregatedMetric;
import com.codbid.telemetry.aggregator.model.aggregation.AggregationKey;
import com.codbid.telemetry.aggregator.model.aggregation.WindowState;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class AggregatedMetricCalculator {

    private final PercentileCalculator percentileCalculator;

    public AggregatedMetricCalculator(PercentileCalculator percentileCalculator) {
        this.percentileCalculator = percentileCalculator;
    }

    public AggregatedMetric calculate(AggregationKey key, WindowState state) {
        long requestsTotal = state.getRequestsTotal();
        long successTotal = state.getSuccessTotal();
        long errorsTotal = state.getErrorsTotal();

        List<Long> durations = state.getDurationsMsCopy();

        double successRate = requestsTotal == 0 ? 0.0 : (double) successTotal / requestsTotal;
        double errorRate = requestsTotal == 0 ? 0.0 : (double) errorsTotal / requestsTotal;
        double latencyAvg = durations.isEmpty() ? 0.0 : (double) state.getLatencyTotalMs() / durations.size();

        double p95 = percentileCalculator.calculate(durations, 95);
        double p99 = percentileCalculator.calculate(durations, 99);

        double rps = calculateRps(state);

        return new AggregatedMetric(
                key.windowName(),
                state.getWindowStart(),
                state.getWindowEnd(),
                key.service(),
                key.operation(),
                key.kind(),
                key.environment(),
                requestsTotal,
                successTotal,
                errorsTotal,
                errorRate,
                successRate,
                latencyAvg,
                state.getLatencyMinMs(),
                state.getLatencyMaxMs(),
                p95,
                p99,
                rps
        );
    }

    private double calculateRps(WindowState state) {
        var now = java.time.Instant.now();

        var effectiveEnd = now.isBefore(state.getWindowEnd())
                ? now
                : state.getWindowEnd();

        double seconds = Duration
                .between(state.getWindowStart(), effectiveEnd)
                .toMillis() / 1000.0;

        return seconds <= 0 ? 0.0 : (double) state.getRequestsTotal() / seconds;
    }
}
