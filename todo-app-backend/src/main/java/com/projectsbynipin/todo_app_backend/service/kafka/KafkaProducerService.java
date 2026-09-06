package com.projectsbynipin.todo_app_backend.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendMessage(String topic, T message) {
        kafkaTemplate.send(topic, message).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Failed to route message to kafka topic [{}]:", topic, ex);
            }
        });
    }
}
