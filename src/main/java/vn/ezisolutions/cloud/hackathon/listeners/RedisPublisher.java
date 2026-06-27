package vn.ezisolutions.cloud.hackathon.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper;


    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    public void publishObject(String channel, Object message) throws JsonProcessingException {
        redisTemplate.convertAndSend(channel, mapper.writeValueAsString(message));
    }
}
