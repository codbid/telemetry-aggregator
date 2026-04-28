package com.codbid.telemetry.aggregator.kafka;

import com.codbid.telemetry.aggregator.model.event.TelemetryEvent;
import com.codbid.telemetry.aggregator.service.AggregationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class TelemetryEventConsumer {

    private final ObjectMapper objectMapper;
    private final AggregationService aggregationService;

    public TelemetryEventConsumer(ObjectMapper objectMapper, AggregationService aggregationService) {
        this.objectMapper = objectMapper;
        this.aggregationService = aggregationService;
    }

    @KafkaListener(
            topics = "#{@telemetryKafkaProperties.topics}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            TelemetryEvent event = objectMapper.readValue(record.value(), TelemetryEvent.class);

            aggregationService.process(event);
            ack.acknowledge();
        } catch (Exception e) {
            System.err.println("Failed to process telemetry event from Kafka");
            System.err.println("topic=" + record.topic()
                    + ", key=" + record.key()
                    + ", partition=" + record.partition()
                    + ", offset=" + record.offset());
            System.err.println("payload=" + record.value());
            e.printStackTrace();

            ack.acknowledge();
        }
    }
}
