package com.codbid.telemetry.aggregator.metrics;

import com.codbid.telemetry.aggregator.service.AggregationService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

@Component
public class AggregationMetricsBinder implements MeterBinder {

    private final AggregationService aggregationService;

    public AggregationMetricsBinder(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder(
                        "telemetry_aggregation_windows",
                        aggregationService,
                        service -> service.findAllAggregates().size()
                )
                .description("Current number of aggregation windows stored in memory")
                .register(registry);
    }
}