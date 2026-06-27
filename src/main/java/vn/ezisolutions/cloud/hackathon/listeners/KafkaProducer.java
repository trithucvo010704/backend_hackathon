package vn.ezisolutions.cloud.hackathon.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.ezisolutions.cloud.hackathon.core.dto.KafkaSystemEvent;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public void sendMessage(String topic, String key, Object message) throws JsonProcessingException {
        String msgStr = this.mapper.writeValueAsString(message);
        log.info(String.format("Send refKey %s to topic %s message: %s", key, topic, msgStr));
        this.kafkaTemplate.send(topic, key, msgStr);
    }

    public void sendEvent(String topic, String key, KafkaSystemEvent event) throws JsonProcessingException {
        this.sendMessage(topic, key, event);
    }
}
