package com.codbid.telemetry.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "telemetry.kafka")
public class TelemetryKafkaProperties {

    private List<String> topics = new ArrayList<>();

    public List<String> getTopics() {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalStateException("No Kafka topics configured");
        }

        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
