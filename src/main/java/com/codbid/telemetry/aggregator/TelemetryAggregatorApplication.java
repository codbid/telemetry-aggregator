package com.codbid.telemetry.aggregator;

import com.codbid.telemetry.aggregator.config.AggregationProperties;
import com.codbid.telemetry.aggregator.config.TelemetryKafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableConfigurationProperties({AggregationProperties.class, TelemetryKafkaProperties.class})
public class TelemetryAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemetryAggregatorApplication.class, args);
    }

}
