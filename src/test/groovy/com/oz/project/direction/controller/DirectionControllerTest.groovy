package com.oz.project.direction.controller

import com.oz.project.direction.service.DirectionService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

class DirectionControllerTest extends Specification {

    private MockMvc mockMvc
    private DirectionService directionService = Mock()

    def setup() {
        // Initialize the mock MVC and other necessary components
        mockMvc = MockMvcBuilders.standaloneSetup(new DirectionController(directionService)).build()
    }

    def "GET /dir/{encodedId}"() {

        given:
        String encodedId = "r"

        String redirectURL = "https://map.kakao.com/link/map/placeName,37.5665,126.978"

        when:
        directionService.findDirectionUrlById(encodedId) >> redirectURL // 스터빙
        def result = mockMvc.perform(get("/dir/${encodedId}", encodedId))

        then:
        result.andExpect {
            status().is3xxRedirection() // 리다이렉트 발생 확인
            redirectedUrl(redirectURL) // 리다이렉트 URL 경로 검증
        }.andDo(print())
    }
}
