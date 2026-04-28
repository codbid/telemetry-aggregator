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

        double windowSeconds = Duration.between(state.getWindowStart(), state.getWindowEnd()).toSeconds();

        double rps = windowSeconds <= 0 ? 0.0 : (double) requestsTotal / windowSeconds;

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
}
