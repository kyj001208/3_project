package com.project.memmem.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.memmem.domain.dto.naver.WeatherInfoDTO;
import com.project.memmem.domain.dto.naver.WeeklyForecastDTO;
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
	        List<WeeklyForecastDTO> weeklyForecast = weatherNaverService.getWeeklyForecast(city);
	        
	        if (weatherInfo != null) {
	            model.addAttribute("weatherInfo", weatherInfo);
	            model.addAttribute("city", city);
	            model.addAttribute("weeklyForecast", weeklyForecast);
	        } else {
	            model.addAttribute("error", "날씨 정보를 가져오는데 실패했습니다.");
	        }
	        
	        // 여러 도시의 날씨 정보 가져오기
            List<String> cities = Arrays.asList("서울", "부산", "대구", "인천", "광주");
            Map<String, WeatherInfoDTO> weatherMap = weatherNaverService.getMultipleWeatherInfo(cities);
            model.addAttribute("weatherMap", weatherMap);
            
	    } catch (Exception e) {
	        System.err.println("Error in getWeather: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("error", "날씨 정보를 가져오는데 실패했습니다: " + e.getMessage());
	    }
	    return "views/naver/weather";
	}
	
	@GetMapping("/weather/multiple")
    public String getMultipleWeather(Model model) {
        try {
            List<String> cities = Arrays.asList("서울", "부산", "대구", "인천", "광주");
            Map<String, WeatherInfoDTO> weatherMap = weatherNaverService.getMultipleWeatherInfo(cities);
            model.addAttribute("weatherMap", weatherMap);

            // 주간 예보는 서울 기준으로 가져오기
            List<WeeklyForecastDTO> weeklyForecast = weatherNaverService.getWeeklyForecast("서울");
            model.addAttribute("weeklyForecast", weeklyForecast);
        } catch (Exception e) {
            System.err.println("Error in getMultipleWeather: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "날씨 정보를 가져오는데 실패했습니다: " + e.getMessage());
        }
        return "views/naver/multipleWeather";
    }
}
