package com.project.memmem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.memmem.domain.dto.user.WeatherInfoDTO;
import com.project.memmem.service.WeatherNaverService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WeatherNaverController {
	
	private final WeatherNaverService weatherNaverService;

	@GetMapping("/weather")
	public String getWeather(@RequestParam(name = "city", defaultValue = "서울") String city, Model model) {
	    try {
	        WeatherInfoDTO weatherInfo = weatherNaverService.getWeatherInfo(city);
	        System.out.println("WeatherInfo: " + weatherInfo);
	        if (weatherInfo != null) {
	            model.addAttribute("weatherInfo", weatherInfo);
	            model.addAttribute("city", city);
	        } else {
	            model.addAttribute("error", "날씨 정보를 가져오는데 실패했습니다.");
	        }
	    } catch (Exception e) {
	        System.err.println("Error in getWeather: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("error", "날씨 정보를 가져오는데 실패했습니다: " + e.getMessage());
	    }
	    return "views/naver/weather";
	}
}
