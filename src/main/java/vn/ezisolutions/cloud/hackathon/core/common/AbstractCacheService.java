package vn.ezisolutions.cloud.hackathon.core.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Getter
@Slf4j
public abstract class AbstractCacheService {
    private final RedisClient client;
    private final String prefix;

    public AbstractCacheService(RedisClient client, String prefix) {
        this.client = client;
        this.prefix = prefix;
    }

    public <T> void cache(String namespace, String id, T object) {
        try {
            client.setObject(String.format("%s:%s:%s", prefix, namespace, id), object);
        } catch (JsonProcessingException e) {
            log.error(String.format("Cannot auth %s:%s:%s: %s", prefix, namespace, id, e.getMessage()));
        }
    }

    public <T> T get(String namespace, String id, Class<T> tClass) {
        try {
            return client.getObject(String.format("%s:%s:%s", prefix, namespace, id), tClass);
        } catch (JsonProcessingException e) {
            log.error(String.format("Cannot get from auth %s:%s:%s: %s", prefix, namespace, id, e.getMessage()));
            return null;
        }
    }


    public <T> void cacheHashMap(String namespace, String id, T object) {
        String key = String.format("%s:%s", prefix, namespace);
        log.info(String.format("auth %s - %s", key, id));
        try {
            client.hSet(key, id, object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void cacheHashMap(String namespace, String id, T object, Long durationSeconds) {
        String key = String.format("%s:%s", prefix, namespace);
        log.info(String.format("auth %s - %s", key, id));
        try {
            client.hSet(key, id, object);
            client.hSetExpireAt(key, id, Instant.now().plusSeconds(durationSeconds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getHashMap(String namespace, String id, Class<T> tClass) {
        String key = String.format("%s:%s", prefix, namespace);
        log.info(String.format("get %s - %s", key, id));
        try {
            if (client.hHas(key, id)) {
                return client.hGet(key, id, tClass);
            }
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
