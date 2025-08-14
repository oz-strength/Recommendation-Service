package com.oz.project.spot.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.project.spot.dto.SpotDto;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotRedisTemplateService {

    private static final String CACHE_KEY = "SPOT";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(SpotDto spotDto) {
        if (Objects.isNull(spotDto) || Objects.isNull(spotDto.getId())) {
            log.error("SpotDto or SpotDto ID is null, not saving to cache.");
            return;
        }

        try {
            hashOperations.put(CACHE_KEY,
                    spotDto.getId().toString(),
                    serializeSpotDto(spotDto));
            log.info("[SpotRedisTemplateService][save] save success - id:  {}", spotDto.getId());
        } catch (JsonProcessingException e) {
            log.error("[SpotRedisTemplateService][save] save error - {}", e.getMessage());
        }
    }

    public List<SpotDto> findAll() {

        try {
            List<SpotDto> list = new ArrayList<>();
            for (String value : hashOperations.entries(CACHE_KEY).values()) {
                SpotDto spotDto = deserializeSpotDto(value);
                list.add(spotDto);
            }
            return list;
        } catch (Exception e) {
            log.error("[SpotRedisTemplateService][findAll] Error deserializing spots - {}", e.getMessage());
            return List.of();
        }
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[SpotRedisTemplateService][delete] delete success - id:  {}", id);
    }

    private String serializeSpotDto(Object spotDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(spotDto);
    }

    private SpotDto deserializeSpotDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, SpotDto.class);
    }
}
