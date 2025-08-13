package com.oz.project.spot.cache

import com.oz.project.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class RedisTemplateTest extends AbstractIntegrationContainerBaseTest{

    @Autowired
    private RedisTemplate redisTemplate

    def "RedisTemplate String operations"() {
        given: "A key and value"
        String key = "testKey"
        String value = "testValue"

        when: "Setting a value in Redis"
        redisTemplate.opsForValue().set(key, value)

        then: "The value can be retrieved"
        redisTemplate.opsForValue().get(key) == value

        when: "Deleting the key"
        redisTemplate.delete(key)

        then: "The key no longer exists"
        redisTemplate.hasKey(key) == false
    }

    def "RedisTemplate set operations"() {
        given:
        def setOperations = redisTemplate.opsForSet()
        def key = "setKey"

        when:
        setOperations.add(key, "h", "e", "l", "l", "o")

        then:
        def size = setOperations.size(key)
        size == 4
    }

    def "RedisTemplate set operations2"() {
        given: "A set key and values"
        String setKey = "testSet"
        String value1 = "value1"
        String value2 = "value2"

        when: "Adding values to the set"
        redisTemplate.opsForSet().add(setKey, value1, value2)

        then: "The set contains the added values"
        redisTemplate.opsForSet().isMember(setKey, value1) == true
        redisTemplate.opsForSet().isMember(setKey, value2) == true

        when: "Removing a value from the set"
        redisTemplate.opsForSet().remove(setKey, value1)

        then: "The removed value is no longer in the set"
        redisTemplate.opsForSet().isMember(setKey, value1) == false
    }

    def "RedisTemplate hash operations"() {
        given:
        def hashOperations = redisTemplate.opsForHash()
        def key = "hashKey"

        when:
        hashOperations.put(key, "subKey", "value")

        then:
        def result = hashOperations.get(key, "subKey")
        result == "value"

        def entries = hashOperations.entries(key)
        entries.keySet().contains("subKey")
        entries.values().contains("value")
        entries.size() == 1

        def size = hashOperations.size(key)
        size == 1

    }

    def "RedisTemplate hash operations2"() {
        given: "A hash key and field-value pairs"
        String hashKey = "testHash"
        String field1 = "field1"
        String value1 = "value1"
        String field2 = "field2"
        String value2 = "value2"

        when: "Setting values in the hash"
        redisTemplate.opsForHash().put(hashKey, field1, value1)
        redisTemplate.opsForHash().put(hashKey, field2, value2)

        then: "The values can be retrieved from the hash"
        redisTemplate.opsForHash().get(hashKey, field1) == value1
        redisTemplate.opsForHash().get(hashKey, field2) == value2

        when: "Deleting a field from the hash"
        redisTemplate.opsForHash().delete(hashKey, field1)

        then: "The deleted field no longer exists in the hash"
        redisTemplate.opsForHash().hasKey(hashKey, field1) == false
    }
}
