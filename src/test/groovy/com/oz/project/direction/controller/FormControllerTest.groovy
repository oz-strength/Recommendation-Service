package com.oz.project.direction.controller

import com.oz.project.direction.dto.OutputDto
import com.oz.project.spot.service.SpotRecommendationService
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class FormControllerTest extends Specification {

    private MockMvc mockMvc
    private SpotRecommendationService spotRecommendationService = Mock()
    private List<OutputDto> outputDtoList

    def setup() {
        // FormController MockMvc 객체로 만든다.
        mockMvc = standaloneSetup(new FormController(spotRecommendationService)).build()

        outputDtoList = new ArrayList<>()
        outputDtoList.addAll(
                OutputDto.builder()
                        .spotName("spot1")
                        .build(),
                OutputDto.builder()
                        .spotName("spot2")
                        .build()
        )
    }

    def "GET / should return main view"() {
        expect:
        mockMvc.perform(get("/"))
                .andExpect {
                    handler().handlerType(FormController.class)
                    handler().methodName("main")
                    status().isOk()
                    view().name("main")
                }
                .andDo(print())
    }

    def "POST /search"() {
        given:
        String inputAddress = "서울시 강남구 역삼동"

        when:
        def resultActions = mockMvc.perform(post("/search")
                .param("address", inputAddress))

        then:
        1 * spotRecommendationService.recommendSpotList(argument -> {
            assert argument == inputAddress
        }) >> outputDtoList


        resultActions
                .andExpect {
                    status().isOk()
                    view().name("output")
                    model().attributeExists("outputFormList") // model 에 outputFormList 라는 key가 존재해야 함
                    model().attribute("outputFormList", outputDtoList)
                }
                .andDo(print())
    }


}
