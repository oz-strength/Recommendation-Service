package com.oz.project.direction.service

import spock.lang.Specification

class Base62ServiceTest extends Specification {

    private Base62Service base62Service

    def setup() {
        base62Service = new Base62Service()
    }

    def "check base62 encoder/decoder"() {
        given: "a number to encode and decode"
        long number = 5L

        when: "the number is encoded to base62"
        String encoded = base62Service.encodeDirectionId(number)

        then: "the encoded value is not null or empty"
        encoded != null && !encoded.isEmpty()

        when: "the encoded value is decoded back to a number"
        long decoded = base62Service.decodeDirectionId(encoded)

        then: "the decoded value matches the original number"
        decoded == number
    }
}
