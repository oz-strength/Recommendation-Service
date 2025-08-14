package com.oz.project.spot.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.project.api.dto.KakaoApiResponseDto;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressSearchRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "address:";

    public Optional<KakaoApiResponseDto> getCachedAddress(String address) {
        String cacheKey = buildCacheKey(address);
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);

        if (cachedJson != null) {
            try {
                KakaoApiResponseDto cachedDto = objectMapper.readValue(cachedJson, KakaoApiResponseDto.class);
                return Optional.of(cachedDto);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize cached value for address: {}", address, e);
            }
        }
        return Optional.empty();
    }

    public void saveAddressCache(String address, KakaoApiResponseDto response, Duration ttl) {
        try {
            String cacheKey = buildCacheKey(address);
            String jsonValue = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, jsonValue, ttl);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize response for address: {}", address, e);
        }
    }

    private String buildCacheKey(String address) {
        return CACHE_KEY_PREFIX + address.trim();
    }
}
