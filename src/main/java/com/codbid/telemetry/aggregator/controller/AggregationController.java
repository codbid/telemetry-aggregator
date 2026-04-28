package com.codbid.telemetry.aggregator.controller;

import com.codbid.telemetry.aggregator.model.aggregation.AggregatedMetric;
import com.codbid.telemetry.aggregator.service.AggregationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AggregationController {

    private final AggregationService aggregationService;

    public AggregationController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/api/aggregates")
    public List<AggregatedMetric> getAll() {
        return aggregationService.findAllAggregates();
    }
}
