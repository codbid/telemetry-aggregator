package com.codbid.telemetry.aggregator.metrics;

import com.codbid.telemetry.aggregator.model.aggregation.AggregatedMetric;
import com.codbid.telemetry.aggregator.service.AggregationService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToDoubleFunction;

@Component
public class AggregationPrometheusExporter {

    private final AggregationService aggregationService;

    private final MultiGauge requests;
    private final MultiGauge success;
    private final MultiGauge errors;

    private final MultiGauge successRate;
    private final MultiGauge errorRate;

    private final MultiGauge latencyAvgMs;
    private final MultiGauge latencyMinMs;
    private final MultiGauge latencyMaxMs;
    private final MultiGauge latencyP95Ms;
    private final MultiGauge latencyP99Ms;

    private final MultiGauge rps;

    public AggregationPrometheusExporter(
            AggregationService aggregationService,
            MeterRegistry meterRegistry
    ) {
        this.aggregationService = aggregationService;

        this.requests = MultiGauge.builder("telemetry_window_requests")
                .description("Requests count in the latest aggregation window")
                .register(meterRegistry);

        this.success = MultiGauge.builder("telemetry_window_success")
                .description("Successful requests count in the latest aggregation window")
                .register(meterRegistry);

        this.errors = MultiGauge.builder("telemetry_window_errors")
                .description("Failed requests count in the latest aggregation window")
                .register(meterRegistry);

        this.errorRate = MultiGauge.builder("telemetry_window_error_rate")
                .description("Error rate in the latest aggregation window")
                .register(meterRegistry);

        this.successRate = MultiGauge.builder("telemetry_window_success_rate")
                .description("Success rate in the latest aggregation window")
                .register(meterRegistry);

        this.latencyAvgMs = MultiGauge.builder("telemetry_window_latency_avg_ms")
                .description("Average latency in milliseconds in the latest aggregation window")
                .register(meterRegistry);

        this.latencyMinMs = MultiGauge.builder("telemetry_window_latency_min_ms")
                .description("Minimum latency in milliseconds in the latest aggregation window")
                .register(meterRegistry);

        this.latencyMaxMs = MultiGauge.builder("telemetry_window_latency_max_ms")
                .description("Maximum latency in milliseconds in the latest aggregation window")
                .register(meterRegistry);

        this.latencyP95Ms = MultiGauge.builder("telemetry_window_latency_p95_ms")
                .description("P95 latency in milliseconds in the latest aggregation window")
                .register(meterRegistry);

        this.latencyP99Ms = MultiGauge.builder("telemetry_window_latency_p99_ms")
                .description("P99 latency in milliseconds in the latest aggregation window")
                .register(meterRegistry);

        this.rps = MultiGauge.builder("telemetry_window_rps")
                .description("Requests per second in the latest aggregation window")
                .register(meterRegistry);
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedDelayString = "${telemetry.metrics.refresh-interval:5s}")
    public void refresh() {
        requests.register(rows(AggregatedMetric::requestsTotal), true);
        success.register(rows(AggregatedMetric::successTotal), true);
        errors.register(rows(AggregatedMetric::errorsTotal), true);

        successRate.register(rows(AggregatedMetric::successRate), true);
        errorRate.register(rows(AggregatedMetric::errorRate), true);

        latencyAvgMs.register(rows(AggregatedMetric::latencyAvgMs), true);
        latencyMinMs.register(rows(metric -> metric.latencyMinMs() == null ? 0.0 : metric.latencyMinMs()), true);
        latencyMaxMs.register(rows(metric -> metric.latencyMaxMs() == null ? 0.0 : metric.latencyMaxMs()), true);
        latencyP95Ms.register(rows(AggregatedMetric::latencyP95Ms), true);
        latencyP99Ms.register(rows(AggregatedMetric::latencyP99Ms), true);

        rps.register(rows(AggregatedMetric::rps), true);
    }

    private List<MultiGauge.Row<Number>> rows(ToDoubleFunction<AggregatedMetric> valueExtractor) {
        return aggregationService.findLatestAggregates().stream()
                .map(metric -> MultiGauge.Row.of(tags(metric), valueExtractor.applyAsDouble(metric)))
                .toList();
    }

    private Tags tags(AggregatedMetric metric) {
        return Tags.of(
                "service", safe(metric.service()),
                "operation", safe(metric.operation()),
                "kind", metric.kind() == null ? "UNKNOWN" : metric.kind().name(),
                "environment", safe(metric.environment()),
                "window", safe(metric.windowName())
        );
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value;
    }
}
