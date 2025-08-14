package com.oz.project.spot.cache;

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
public class TouristSpotRedisService {
    private final RedisTemplate<String, KakaoApiResponseDto> touristSpotRedisTemplate;

    private static final String CACHE_KEY_PREFIX = "spot:";

    public Optional<KakaoApiResponseDto> getCachedSpots(double lat, double lng, double radius) {
        String key = buildCacheKey(lat, lng, radius);
        return Optional.ofNullable(touristSpotRedisTemplate.opsForValue().get(key));
    }

    public void saveSpotsCache(double lat, double lng, double radius, KakaoApiResponseDto dto, Duration ttl) {
        String key = buildCacheKey(lat, lng, radius);
        touristSpotRedisTemplate.opsForValue().set(key, dto, ttl);
    }

    private String buildCacheKey(double lat, double lng, double radius) {
        return CACHE_KEY_PREFIX + round(lat, 4) + ":" + round(lng, 4) + ":" + radius;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
