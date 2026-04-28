package com.codbid.telemetry.aggregator.model.aggregation;

public record LatestAggregateKey(
        String windowName,
        String service,
        String operation,
        String kind,
        String environment
) {

    public static LatestAggregateKey from(AggregatedMetric metric) {
        return new LatestAggregateKey(
                metric.windowName(),
                metric.service(),
                metric.operation(),
                metric.kind().name(),
                metric.environment()
        );
    }
}