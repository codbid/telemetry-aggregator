package com.codbid.telemetry.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "telemetry.aggregation")
public class AggregationProperties {

    private Duration cleanupInterval = Duration.ofSeconds(30);

    private List<WindowProperties> windows = new ArrayList<>();

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public List<WindowProperties> getWindows() {
        return windows;
    }

    public void setWindows(List<WindowProperties> windows) {
        this.windows = windows;
    }

    public static class WindowProperties {

        private String name;
        private Duration duration;
        private Duration retention;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public Duration getRetention() {
            return retention;
        }

        public void setRetention(Duration retention) {
            this.retention = retention;
        }
    }
}