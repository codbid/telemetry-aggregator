package com.codbid.telemetry.aggregator;

import com.codbid.telemetry.aggregator.config.AggregationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AggregationProperties.class)
public class TelemetryAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryAggregatorApplication.class, args);
    }

}
