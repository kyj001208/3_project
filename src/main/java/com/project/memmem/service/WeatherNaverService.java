package com.project.memmem.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.project.memmem.domain.dto.naver.HourlyWeatherDTO;
import com.project.memmem.domain.dto.naver.WeatherInfoDTO;
import com.project.memmem.domain.dto.naver.WeeklyForecastDTO;

public interface WeatherNaverService {
	
	WeatherInfoDTO getWeatherInfo(String city) throws IOException;
	
	List<WeeklyForecastDTO> getWeeklyForecast(String city) throws IOException;
	
	Map<String, WeatherInfoDTO> getMultipleWeatherInfo(List<String> cities) throws IOException;

	List<HourlyWeatherDTO> getHourlyWeather(String city) throws IOException;

}
