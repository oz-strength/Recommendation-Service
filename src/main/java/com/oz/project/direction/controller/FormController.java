package com.oz.project.direction.controller;

import com.oz.project.direction.dto.InputDto;
import com.oz.project.spot.service.SpotRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final SpotRecommendationService spotRecommendationService;

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @PostMapping("/search")
    public ModelAndView search(@ModelAttribute InputDto inputDto) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("output");
        modelAndView.addObject("outputFormList",
                spotRecommendationService.recommendSpotList(inputDto.getAddress()));
        
        return modelAndView;
    }
}
